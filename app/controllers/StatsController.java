package controllers;

import entities.RequestsDaily;
import entities.UrlStats;
import entities.User;
import entities.mongodb.MongoDbRequestsDaily;
import entities.mongodb.MongoDbUrlStats;
import i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.*;

import java.time.LocalDate;
import java.util.*;

public class StatsController extends Controller {

    public Result show(Http.Request request, String period) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }
        String lang = Lang.get(request);

        LocalDate from, to;
        if ("last7".equals(period)) {
            to = LocalDate.now();
            from = to.minusDays(7);
        } else if ("last30".equals(period)) {
            to = LocalDate.now();
            from = to.minusDays(30);
        } else if (period == null || "today".equals(period) || InputUtils.parseDate(period + "-01") == null) {
            from = LocalDate.now();
            to = from.plusDays(1);
            period = "today";
        } else  {
            from = InputUtils.parseDate(period + "-01");
            to = from.plusMonths(1);
        }

        List<? extends RequestsDaily> requestsDaily = context.getRequestsDailyModel().getRange(from, to);
        if (requestsDaily.isEmpty()) {
            throw new NotFoundException("no stats found");
        }

        RequestsDaily accumulated = new MongoDbRequestsDaily();
        requestsDaily.forEach(r -> {
            r.getUrlStats().values().forEach(s -> {
                if (!accumulated.getUrlStats().containsKey(s.getMapKey())) {
                    accumulated.getUrlStats().put(s.getMapKey(), s);
                } else {
                    int count = accumulated.getUrlStats().get(s.getMapKey()).getCount();
                    accumulated.getUrlStats().put(s.getMapKey(), new MongoDbUrlStats(s.getUrl(), s.getReferer(), s.getCount() + count));
                }
            });
        });

        List<String> selectableMonths = new ArrayList<>();
        LocalDate earliestMonth = context.getRequestsDailyModel().getFirstDate().withDayOfMonth(1);
        LocalDate month = LocalDate.now().withDayOfMonth(1);
        do {
            selectableMonths.add(month.toString().substring(0, 7));
            month = month.minusMonths(1);
        } while (!month.isBefore(earliestMonth));

        Set<String> domains = new HashSet<>();

        String hostEn = Config.Option.HOST_EN.get();
        String hostDe = Config.Option.HOST_DE.get();
        List<? extends UrlStats> externalReferers = accumulated.getUrlStats().values().stream().filter(s -> {
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
            domains.add(domain);
            return true;
        }).toList();

        Set<String> consolidated = new HashSet<>();
        Map<String, String> consolidatorLookup = new HashMap<>();
        domains.stream().sorted(Comparator.comparing(String::length)).forEach(domain -> {
            int p = domain.lastIndexOf(".");
            while (p > 0) {
                String upperDomainPart = domain.substring(p + 1);
                if (consolidated.contains(upperDomainPart)) {
                    consolidatorLookup.put(domain, upperDomainPart);
                    return;
                }
                p = domain.substring(0, p).lastIndexOf(".");
            }
            if (domain.startsWith("www.")) {
                String domainShortened = domain.substring(4);
                consolidated.add(domainShortened);
                consolidatorLookup.put(domain, domainShortened);
            } else {
                consolidated.add(domain);
                consolidatorLookup.put(domain, domain);
            }
        });

        Map<String, List<UrlStats>> consolidatedRefererUrlStats = new HashMap<>();
        externalReferers.forEach(s -> {
            String domain = s.getReferer().split("/")[0];
            String consolidatedDomain = consolidatorLookup.get(domain);
            if (!consolidatedRefererUrlStats.containsKey(consolidatedDomain)) {
                consolidatedRefererUrlStats.put(consolidatedDomain, new ArrayList<>());
            }
            consolidatedRefererUrlStats.get(consolidatedDomain).add(s);
        });

        return ok(views.html.stats.view.render(request, selectableMonths, period, consolidatedRefererUrlStats, user, lang));
    }
}
