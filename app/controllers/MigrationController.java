package controllers;

import entities.Photo;
import entities.User;
import entities.tmp.TmpFile;
import play.inject.ApplicationLifecycle;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.*;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MigrationController extends Controller {

    private BahnbilderLogger logger = new BahnbilderLogger(MigrationController.class);

    private volatile boolean stopped = false;

    @Inject
    public MigrationController(ApplicationLifecycle appLifecycle) {
        appLifecycle.addStopHook(() -> { stopped = true; return CompletableFuture.completedFuture(null); });
    }

    private boolean fileMigrationInProgress = false;
    private int fileMigrationThreads = 0;

    private boolean getFileMigrationLock() {
        synchronized(this) {
            if (!fileMigrationInProgress) {
                fileMigrationInProgress = true;
                return true;
            }
            return false;
        }
    }

    private void releaseFileMigrationLock() {
        synchronized(this) {
            fileMigrationThreads--;
            if (fileMigrationThreads == 0) {
                fileMigrationInProgress = false;
            }
        }
    }

    public Result migrateFiles(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null || !user.isAdmin()) {
            throw new NotAllowedException();
        }

        if (getFileMigrationLock()) {
            ConcurrentHashMap.KeySetView<Photo, Boolean> photoSet = ConcurrentHashMap.newKeySet();
            photoSet.addAll(context.getPhotosModel().getAll().toList());
            int total = photoSet.size();

            int threads = 4;
            final AtomicInteger migratedNew = new AtomicInteger();
            final AtomicInteger migratedDeleted = new AtomicInteger();
            final AtomicInteger migratedUnmodified = new AtomicInteger();
            final AtomicInteger migratedModified = new AtomicInteger();
            while (fileMigrationThreads < threads) {
                fileMigrationThreads++;
                new Thread(() -> {
                    try {
                        while (!photoSet.isEmpty() && !stopped) {
                            Photo photo = photoSet.stream().findAny().orElse(null);
                            if (photo != null && photoSet.remove(photo)) {
                                try {
                                    TmpFile.Status status = context.getFilesOriginalModel().ensureMigrated(photo);
                                    if (status == TmpFile.Status.NEW) {
                                        migratedNew.incrementAndGet();
                                    } else if (status == TmpFile.Status.DELETED) {
                                        migratedDeleted.incrementAndGet();
                                    } else if (status == TmpFile.Status.UNMODIFIED) {
                                        migratedUnmodified.incrementAndGet();
                                    } else if (status == TmpFile.Status.MODIFIED) {
                                        migratedModified.incrementAndGet();
                                    }
                                } catch (Exception e) {
                                    logger.error(context.getRequest(), e);
                                    // something went wrong, sleep 10 seconds to give the server some time to recover
                                    Thread.sleep(10 * 1000l);
                                    photoSet.add(photo); // re-queue photo
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        // something interrupted our thread's sleep, exit
                    }
                    releaseFileMigrationLock();
                }).start();
            }

            new Thread(() -> {
                try {
                    while (!photoSet.isEmpty() && !stopped) {
                        logger.info(context.getRequest(), "Files migration " + (total - photoSet.size()) + " of " + total + " done (unmodified " + migratedUnmodified + ", modified " + migratedModified + ", new " + migratedNew + ", deleted " + migratedDeleted + ")");
                        Thread.sleep(5 * 1000l);
                    }
                } catch (InterruptedException e) {
                    // something interrupted our thread's sleep, exit
                }
            }).start();
        } else {
            throw new AlreadyInProgressException("file migration");
        }

        return ok();
    }
}
