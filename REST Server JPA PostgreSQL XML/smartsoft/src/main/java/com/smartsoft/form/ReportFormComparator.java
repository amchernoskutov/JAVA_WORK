package com.smartsoft.form;

import java.util.Comparator;

public class ReportFormComparator implements Comparator<ReportForm> {

  public int compare(ReportForm a, ReportForm b) {
    if (a.getPurchasedate().compareTo(b.getPurchasedate()) == 0) {
      return -1;
    } else {
      return a.getPurchasedate().compareTo(b.getPurchasedate());
    }
  }
}
