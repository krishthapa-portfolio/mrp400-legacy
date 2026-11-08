/*
 * Main.java
 *
 * Batch entry point. Runs one transaction and prints KEY=VALUE lines.
 *
 *   java -cp classes com.mpp.mrp.Main <TRANSACTION> [KEY=VALUE ...]
 *
 * Transactions:
 *   GET-PART              PART_ID
 *   LIST-REORDER
 *   GET-BOM               PART_ID
 *   CALC-MATERIAL-NEEDS   PART_ID QTY
 *   GET-WORK-ORDER        WORK_ORDER_ID
 *   LIST-OPEN-WORK-ORDERS
 *   GET-MACHINE-STATUS    MACHINE_ID
 *   CREATE-WORK-ORDER     WORK_ORDER_ID PART_ID QTY DUE_DATE MACHINE_ID   (writes)
 *
 * CHANGE LOG
 *   1999-06-21  RTH  Original version, replaced green-screen menu.
 *   2004-10-12  KLM  KEY=VALUE arguments instead of positional ones.
 */
package com.mpp.mrp;

import java.util.Hashtable;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("STATUS=NO-TRANSACTION\n");
            System.exit(2);
        }
        Hashtable p = new Hashtable();
        for (int i = 1; i < args.length; i++) {
            int eq = args[i].indexOf('=');
            if (eq > 0) {
                p.put(args[i].substring(0, eq).toUpperCase(), args[i].substring(eq + 1));
            }
        }
        try {
            System.out.print(run(args[0].toUpperCase(), p));
        } catch (Exception e) {
            System.out.print("STATUS=ERROR\n");
            System.exit(1);
        }
    }

    static String run(String tx, Hashtable p) throws Exception {
        if ("GET-PART".equals(tx)) {
            return PartMaster.getPart(get(p, "PART_ID"));
        }
        if ("LIST-REORDER".equals(tx)) {
            return PartMaster.listReorder();
        }
        if ("GET-BOM".equals(tx)) {
            return BillOfMaterials.getBom(get(p, "PART_ID"));
        }
        if ("CALC-MATERIAL-NEEDS".equals(tx)) {
            return BillOfMaterials.calcMaterialNeeds(get(p, "PART_ID"), toInt(get(p, "QTY")));
        }
        if ("GET-WORK-ORDER".equals(tx)) {
            return WorkOrders.getWorkOrder(get(p, "WORK_ORDER_ID"));
        }
        if ("LIST-OPEN-WORK-ORDERS".equals(tx)) {
            return WorkOrders.listOpenWorkOrders();
        }
        if ("GET-MACHINE-STATUS".equals(tx)) {
            return Machines.getMachineStatus(get(p, "MACHINE_ID"));
        }
        if ("CREATE-WORK-ORDER".equals(tx)) {
            return WorkOrders.createWorkOrder(get(p, "WORK_ORDER_ID"), get(p, "PART_ID"),
                toInt(get(p, "QTY")), get(p, "DUE_DATE"), get(p, "MACHINE_ID"));
        }
        return "STATUS=UNKNOWN-TRANSACTION\n";
    }

    static String get(Hashtable p, String key) {
        String v = (String) p.get(key);
        return v == null ? "" : v.trim().toUpperCase();
    }

    static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
