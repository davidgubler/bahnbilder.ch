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

import java.util.*;
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

        Set<String> domains = new HashSet<>();

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
            consolidated.add(domain);
            consolidatorLookup.put(domain, domain);
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

        return ok(views.html.stats.view.render(request, consolidatedRefererUrlStats, user, lang));
    }
}
