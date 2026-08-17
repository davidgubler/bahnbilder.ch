package biz;

import entities.DnsRecord;
import entities.User;
import utils.*;

import java.net.URI;
import java.util.*;

public class Dns {

    private BahnbilderLogger logger = new BahnbilderLogger(Dns.class);

    private boolean allRecordsAreInDomain(Collection<DnsRecord> dnsRecords, String domain) {
        for (DnsRecord r : dnsRecords) {
            if (!r.getName().equals(domain) && !r.getName().endsWith("." + domain)) {
                return false;
            }
        }
        return true;
    }

    private String extractDomain(Collection<? extends DnsRecord> dnsRecords) {
        if (dnsRecords.isEmpty()) {
            return null;
        }
        List<DnsRecord> records = new ArrayList<>(dnsRecords);
        records.sort(Comparator.comparingInt(r -> r.getName().length()));

        LinkedList<String> labels = new LinkedList<>(Arrays.asList(records.get(0).getName().split("\\.")));
        while (labels.size() > 2) {
            if (allRecordsAreInDomain(records, String.join(".", labels))) {
                return String.join(".", labels);
            }
            labels.removeFirst();
        }
        return String.join(".", labels);
    }

    public void check(Context context, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        String zoneId = Config.Option.CLOUDFLARE_ZONE_ID.get();
        if (zoneId == null) {
            errors.put("zoneId", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        List<? extends DnsRecord> records = context.getDnsModel().getRecords(zoneId);
        String domain = extractDomain(records);
        for (String failoverHost : Config.Option.CLOUDFLARE_FAILOVER_HOSTS.getStrings()) {
            String testHost = failoverHost + "." + domain;
            String ip = records.stream().filter(r -> r.getName().equals(testHost)).filter(r -> "AAAA".equals(r.getType())).map(DnsRecord::getContent).findFirst().orElse(null);
            if (ip == null) {
                logger.info(context.getRequest(), "No AAAA entry for " + testHost);
                continue;
            }
            DnsRecord hostRecord = records.stream().filter(r -> r.getName().equals(domain)).filter(r -> "AAAA".equals(r.getType())).filter(r -> ip.equals(r.getContent())).findFirst().orElse(null);
            URI uri;
            try {
                uri = new URI("https://" + testHost + "/status");
            } catch (Exception e) {
                // doesn't happen
                throw new RuntimeException();
            }
            boolean check = context.getDnsModel().check(uri);

            if (failoverHost.equals(Config.getHostname())) {
                if (check && hostRecord == null) {
                    logger.info(context.getRequest(), "Failover host " + testHost + " responds, adding to DNS");
                    createRecord(context, domain, null, "AAAA", ip, true, user);
                }
            } else {
                if (!check && hostRecord != null) {
                    logger.info(context.getRequest(), "Failover host " + testHost + " not responding, removing from DNS");
                    deleteRecord(context, hostRecord, user);
                }
            }
        }
    }

    public void deleteRecord(Context context, DnsRecord record, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        String zoneId = Config.Option.CLOUDFLARE_ZONE_ID.get();
        if (zoneId == null) {
            errors.put("zoneId", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        context.getDnsModel().deleteRecord(zoneId, record);

        // LOG
        logger.info(context.getRequest(), user + " removed DNS record " + record);
    }

    public DnsRecord createRecord(Context context, String name, Integer ttl, String type, String content, Boolean proxied, User user) throws ValidationException {
        // ACCESS
        if (user == null) {
            throw new NotAllowedException();
        }

        // INPUT
        Map<String, String> errors = new HashMap<>();
        String zoneId = Config.Option.CLOUDFLARE_ZONE_ID.get();
        if (zoneId == null) {
            errors.put("zoneId", ErrorMessages.MISSING_VALUE);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // BUSINESS
        DnsRecord record = context.getDnsModel().createRecord(zoneId, name, ttl, type, content, proxied);

        // LOG
        logger.info(context.getRequest(), user + " created DNS record " + record);

        return record;
    }
}
