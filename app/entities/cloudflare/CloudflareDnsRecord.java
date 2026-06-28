package entities.cloudflare;

import entities.DnsRecord;

public class CloudflareDnsRecord implements DnsRecord {
    private String id;
    private String name;
    private Integer ttl;
    private String type;
    private String content;
    private Boolean proxiable;
    private Boolean proxied;

    public CloudflareDnsRecord() {
        // empty constructor for deserializer
    }

    public CloudflareDnsRecord(String name, Integer ttl, String type, String content, Boolean proxied) {
        this.name = name;
        this.ttl = ttl;
        this.type = type;
        this.content = content;
        this.proxied = proxied;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return name + " " + ttl + " IN " + type + " " + content;
    }
}
