package com.manager.guiApp;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import com.manager.steamitems.SteamItem;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GUIApp {
    private static GUIApp instance;
    private final JFrame frame;

    private GUIApp() {
        frame = new JFrame("Steam Market Manager 3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);

        frame.pack();
        frame.setVisible(true);
    }

    public static GUIApp getInstance() {
        if (instance == null) {
            instance = new GUIApp();
        }

        return instance;
    }

    public void displayArrayListLive(ArrayList<SteamItem> items) {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

        scheduler.scheduleAtFixedRate(() -> {
            UpdateListDisplay(listModel, items);
        }, 0, 3, TimeUnit.SECONDS);

        JList<String> itemJList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(itemJList);

        frame.add(scrollPane);
    }

    private void UpdateListDisplay(DefaultListModel<String> listModel, ArrayList<SteamItem> items) {
        ArrayList<String> itemStrings = ParseItemsToStrings(items);

        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (var itemStr : itemStrings) {
                listModel.addElement(itemStr);
            }
        });
    }

    private ArrayList<String> ParseItemsToStrings(ArrayList<SteamItem> items) {
        ArrayList<String> itemStrings = new ArrayList<>();

        for (var item : items) {
            float price = item.getPricePerPiece();
            if (price == -1) {
                itemStrings.add(item.getMarketHashName() + " ... | x" + item.getAmount());
                continue;
            }

            itemStrings.add(item.getMarketHashName() + " $" + item.getPricePerPiece() + " | x" + item.getAmount() + " = $" + item.getTotalPrice());
        }

        return itemStrings;
    }
}
