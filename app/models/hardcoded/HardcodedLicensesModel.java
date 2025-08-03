package models.hardcoded;

import entities.License;
import entities.hardcoded.HardcodedLicense;
import models.LicensesModel;

import java.util.*;

public class HardcodedLicensesModel implements LicensesModel {
    private static final Map<Integer, HardcodedLicense> licenses;

    private static final List<HardcodedLicense> all;

    private static final HardcodedLicense COPYRIGHT = new HardcodedLicense(1, "©",
            "<a class=\"hoverlink\" href=\"http://de.wikipedia.org/wiki/Urheberrecht\" target=\"_blank\">©</a>",
            "<a class=\"hoverlink\" href=\"https://en.wikipedia.org/wiki/Copyright\" target=\"_blank\">©</a>");

    private static final HardcodedLicense CC_BY = new HardcodedLicense(2, "cc-by",
            "<a href=\"https://creativecommons.org/licenses/by/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by/2.5/ch/80x15.png\" title=\"Creative Commons cc-by\" /></a>");

    private static final HardcodedLicense CC_BY_SA = new HardcodedLicense(3, "cc-by-sa",
            "<a href=\"https://creativecommons.org/licenses/by-sa/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-sa/2.5/ch/80x15.png\" title=\"Creative Commons cc-by-sa\" /></a>");

    private static final HardcodedLicense CC_BY_ND = new HardcodedLicense(4, "cc-by-nd",
            "<a href=\"https://creativecommons.org/licenses/by-nd/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-nd/2.5/ch/80x15.png\" title=\"Creative Commons cc-by-nd\" /></a>");

    private static final HardcodedLicense CC_BY_NC = new HardcodedLicense(5, "cc-by-nc",
            "<a href=\"https://creativecommons.org/licenses/by-nc/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-nc/2.5/ch/80x15.png\" title=\"Creative Commons cc-by-nc\" /></a>");

    private static final HardcodedLicense CC_BY_NC_SA = new HardcodedLicense(6, "cc-by-nc-sa",
            "<a href=\"https://creativecommons.org/licenses/by-nc-sa/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-nc-sa/2.5/ch/80x15.png\" title=\"Creative Commons cc-by-nc-sa\" /></a>");

    private static final HardcodedLicense CC_BY_NC_ND = new HardcodedLicense(7, "cc-by-nc-nd",
            "<a href=\"https://creativecommons.org/licenses/by-nc-nd/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-nc-nd/2.5/ch/80x15.png\" title=\"Creative Commons cc-by-nc-nd\" /></a>");

    private static final HardcodedLicense CC_BY_NC_SA_GFDL = new HardcodedLicense(8, "cc-by-nc-sa, GFDL",
            "<a href=\"https://creativecommons.org/licenses/by-nc-sa/2.5/ch/\" target=\"_blank\"><img src=\"https://licensebuttons.net/l/by-nc-sa/2.5/ch/80x15.png\" title=\"cc-by-nc-sa\" /></a> <a href=\"https://www.gnu.org/copyleft/fdl.html\" target=\"_blank\"><img src=\"/assets/images/gnufdl.png\" title=\"GNU FDL\"></a>");

    private static final HardcodedLicense PUBLIC_DOMAIN = new HardcodedLicense(9, "Public Domain",
            "<a class=\"hoverlink\" href=\"http://de.wikipedia.org/wiki/Gemeinfreiheit\">Public Domain</a>",
            "<a class=\"hoverlink\" href=\"https://en.wikipedia.org/wiki/Public_domain\">Public domain</a>");

    static {
        licenses = new HashMap<>();
        licenses.put(COPYRIGHT.getId(), COPYRIGHT);
        licenses.put(CC_BY.getId(), CC_BY);
        licenses.put(CC_BY_SA.getId(), CC_BY_SA);
        licenses.put(CC_BY_ND.getId(), CC_BY_ND);
        licenses.put(CC_BY_NC.getId(), CC_BY_NC);
        licenses.put(CC_BY_NC_SA.getId(), CC_BY_NC_SA);
        licenses.put(CC_BY_NC_ND.getId(), CC_BY_NC_ND);
        licenses.put(CC_BY_NC_SA_GFDL.getId(), CC_BY_NC_SA_GFDL);
        licenses.put(PUBLIC_DOMAIN.getId(), PUBLIC_DOMAIN);
        List<HardcodedLicense> tmpAll = new LinkedList<>(licenses.values());
        Collections.sort(tmpAll);
        all = Collections.unmodifiableList(tmpAll);
    }

    public License get(Integer id) {
        return licenses.get(id);
    }

    public List<? extends License> getAll() {
        return all;
    }
}
