package controllers;

import entities.RequestsDaily;
import entities.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.Context;
import utils.NotAllowedException;

public class StatsController extends Controller {
    public Result show(Http.Request request) {
        Context context = Context.get(request);
        User user = context.getUsersModel().getFromRequest(request);
        if (user == null) {
            throw new NotAllowedException();
        }

        RequestsDaily requestsDaily = context.getRequestsDailyModel().getToday();
        if (requestsDaily == null) {
            System.out.println("no stats for today");
        }
        System.out.println("stats records for today: " + requestsDaily.getUrlStats().size());
        return ok();
    }
}
