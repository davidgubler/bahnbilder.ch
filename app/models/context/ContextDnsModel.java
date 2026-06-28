package models.context;

import com.google.inject.Inject;
import entities.DnsRecord;
import models.DnsModel;
import utils.Context;

import java.net.URI;
import java.util.List;

public class ContextDnsModel extends ContextModel implements DnsModel {

    @Inject
    private DnsModel dnsModel;

    public ContextDnsModel(Context context) {
        this.context = context;
    }

    @Override
    public List<? extends DnsRecord> getRecords(String zoneId) {
        return call(() -> dnsModel.getRecords(zoneId));
    }

    @Override
    public void deleteRecord(String zoneId, DnsRecord record) {
        call(() -> { dnsModel.deleteRecord(zoneId, record); return null; });
    }

    @Override
    public DnsRecord createRecord(String zoneId, String name, Integer ttl, String type, String content, Boolean proxied) {
        return call(() -> dnsModel.createRecord(zoneId, name, ttl, type, content, proxied));
    }

    @Override
    public boolean check(URI uri) {
        return call(() -> dnsModel.check(uri));
    }
}
