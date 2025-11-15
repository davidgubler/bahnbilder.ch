package controllers;

import entities.RequestsDaily;
import entities.UrlStats;
import entities.User;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Config;
import utils.Context;
import utils.NotAllowedException;

import java.util.List;
import java.util.stream.Collectors;

public class StatsController extends Controller {
    public Result show(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);

        RequestsDaily requestsDaily = context.getRequestsDailyModel().getToday();
        if (requestsDaily == null) {
            System.out.println("no stats for today");
        }
        System.out.println("stats records for today: " + requestsDaily.getUrlStats().size());

        String hostEn = Config.Option.HOST_EN.get();
        String hostDe = Config.Option.HOST_DE.get();
        List<? extends UrlStats> externalReferers = requestsDaily.getUrlStats().values().stream().filter(s -> {
            if (s.getReferer() == null) {
                return false;
            }
            String domain = s.getReferer().split("/")[0];
            if (hostEn != null && (domain.equals(hostEn) || domain.endsWith("." + hostEn))) {
                return false;
            }
            if (hostDe != null && (domain.equals(hostDe) || domain.endsWith("." + hostDe))) {
                return false;
            }
            return true;
        }).collect(Collectors.toUnmodifiableList());

        return ok(views.html.stats.view.render(request, externalReferers, user, lang));
    }
}
