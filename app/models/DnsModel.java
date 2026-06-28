package models;

import entities.DnsRecord;

import java.net.URI;
import java.util.List;

public interface DnsModel {
    List<? extends DnsRecord> getRecords(String zoneId);

    void deleteRecord(String zoneId, DnsRecord record);

    DnsRecord createRecord(String zoneId, String name, Integer ttl, String type, String content, Boolean proxied);

    boolean check(URI uri);
}
