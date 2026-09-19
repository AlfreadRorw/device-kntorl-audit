package com.audit.device;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellExec {
    public static String run(String cmd) {
        if (cmd == null || cmd.isEmpty()) return "empty";
        StringBuilder out = new StringBuilder();
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd});
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = r.readLine()) != null) {
                out.append(line).append("\n");
                if (out.length() > 5000) break;
            }
            r.close();
            p.waitFor();
        } catch (Exception e) {
            return "err: " + e.getMessage();
        }
        return out.toString();
    }
}
