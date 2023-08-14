package com.taehyunDev;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class fileUtil {
    final String[] vendorList = new String[] { "Asus", "Tyan", "SuperMicro", "Gigabyte", "lanCard" };

    private static final Map<String, Set<String>> vendorMap = new HashMap<>();

    private static fileUtil instance = null;

    public static fileUtil getInstance() {
        if (instance == null)
            instance = new fileUtil();
        return instance;
    }

    public String getVendorForIp(String ipAddr) {
        String returnValue;
        try {
            returnValue = getVendorForMac(getArpMacAddr(ipAddr));
        } catch (Exception e) {
            returnValue = "unkown";
        }
        return returnValue;
    }

    public String getVendorForMac(String macAddr) {
        for (Map.Entry<String, Set<String>> entry : vendorMap.entrySet()) {
            for (String s : entry.getValue()) {
                if (macAddr.toUpperCase().startsWith(s))
                    return entry.getKey();
            }
        }
        return "unkown";
    }

    public String getArpMacAddr(String ipAddr) throws IOException {
        Scanner s = (new Scanner(Runtime.getRuntime().exec("arp -a " + ipAddr).getInputStream())).useDelimiter("\\A");
        String cmdValue = s.next();
        if (cmdValue.contains(ipAddr)) {
            String[] splitText = cmdValue.split("\n");
            Matcher matcher = Pattern.compile("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})").matcher(splitText[3]);
            if (matcher.find())
                return matcher.group();
            return "Error: No Such Mac Addr in Cmd Value!";
        }
        return "Error: No Such Ip in Arp Table!";
    }

    public void readMacVendorData() throws IOException {
        for (String file : this.vendorList) {
            BufferedReader reader = new BufferedReader(new FileReader("./bmc_db/mac/" + file + ".txt"));
            Set<String> tempList = new HashSet<>();
            String str;
            while ((str = reader.readLine()) != null) {
                for (String splitStr : str.split(" "))
                    tempList.add(splitStr.substring(0, 8).replaceAll("x", ""));
            }
            vendorMap.put(file, tempList);
            reader.close();
        }
    }
}
