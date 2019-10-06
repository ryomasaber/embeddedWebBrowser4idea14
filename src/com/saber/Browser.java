package com.saber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Browser extends JPanel {
    private JTextField urlField;

    private BrowserView browserView;

    private BinaryTreeNode<String> history;

    private AtomicBoolean inHistory = new AtomicBoolean(false);

    public Browser(BrowserView browserView) {
        this.browserView = browserView;
        initWebView();

    }

    public JPanel getControllers() {
        JPanel controllers = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        controllers.setLayout(layout);
        urlField = new JTextField();
        JButton buttonPrev = new JButton("<");
        buttonPrev.setPreferredSize(new Dimension(40, 30));
        JButton buttonNext = new JButton(">");
        buttonNext.setPreferredSize(new Dimension(40, 30));
        JButton buttonReload = new JButton("≈");
        buttonReload.setPreferredSize(new Dimension(40, 30));
        controllers.add(buttonPrev);
        controllers.add(buttonNext);
        controllers.add(urlField);
        controllers.add(buttonReload);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonPrev, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonNext, s);
        s.gridwidth = 5;
        s.weightx = 1;
        s.weighty = 0;
        layout.setConstraints(urlField, s);
        s.gridwidth = 1;
        s.weightx = 0;
        s.weighty = 0;
        layout.setConstraints(buttonReload, s);

        urlField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String trim = urlField.getText().trim();
                if (!trim.startsWith("http")) {
                    trim = "http://" + trim;
                }
                browserView.load(trim);
            }
        });
        buttonPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (history != null && history.getLeftChild() != null) {
                    history = history.getLeftChild();
                    if (history.getData() != null) {
                        browserView.load(history.getData().trim());
                        inHistory.set(true);
                    }
                }
            }
        });

        buttonNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (history != null && history.getRightChild() != null) {
                    history = history.getRightChild();
                    if (history.getData() != null) {
                        browserView.load(history.getData().trim());
                        inHistory.set(true);
                    }
                }
            }
        });

        buttonReload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                Browser.this.initWebView();
            }
        });
        return controllers;
    }


    private void initWebView() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Browser.this.removeAll();
                GridBagLayout layout = new GridBagLayout();
                Browser.this.setLayout(layout);
                JComponent controllers = Browser.this.getControllers();
                Browser.this.add(controllers);
                browserView.init();
                JComponent webPanel = browserView.getNode();
                Browser.this.add(webPanel);
                browserView.urlChangeCallback(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        urlField.setText(s);
                        if (!inHistory.get())
                            if (history == null) {
                                history = new BinaryTreeNode<>(s);
                            } else {
                                BinaryTreeNode<String> current = new BinaryTreeNode<>(s);
                                history.setRightChild(current);
                                current.setLeftChild(history);
                                history = history.getRightChild();
                            }
                        inHistory.set(false);
                    }
                });
                GridBagConstraints s = new GridBagConstraints();
                s.fill = GridBagConstraints.BOTH;
                s.gridwidth = 0;
                s.weightx = 1;
                s.weighty = 0;
                layout.setConstraints(controllers, s);
                s.gridwidth = 0;
                s.weightx = 1;
                s.weighty = 1;
                layout.setConstraints(webPanel, s);
                Browser.this.validate();
                Browser.this.repaint();
            }
        });
    }
}
