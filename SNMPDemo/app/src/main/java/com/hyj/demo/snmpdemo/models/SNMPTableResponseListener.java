/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hyj.demo.snmpdemo.models;

/**
 *
 * @author brnunes
 */
public interface SNMPTableResponseListener {
    public void onRowReceived(Object[] row);
    public void onTableReceived();
}
