package com.groupstp.maintenance.web.login;

import com.groupstp.maintenance.config.MaintenanceConfig;
import com.haulmont.cuba.gui.components.BoxLayout;
import com.haulmont.cuba.gui.components.HtmlBoxLayout;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.executors.UIAccessor;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.app.loginwindow.AppLoginWindow;
import com.vaadin.server.*;

import javax.inject.Inject;

/**
 * Extended application login window to support maintenance page
 *
 * @author adiatullin
 */
public class ExtAppLoginWindow extends AppLoginWindow {

    @Inject
    protected BackgroundWorker backgroundWorker;
    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected ScrollBoxLayout maintenanceBox;
    @Inject
    protected BoxLayout loginWrapper;
    @Inject
    protected Label poweredByLink;

    @Inject
    protected MaintenanceConfig maintenanceConfig;

    protected UIAccessor uiAccessor;

    @Override
    public void ready() {
        super.ready();

        uiAccessor = backgroundWorker.getUIAccessor();

        setupMaintenanceRequestHandler();
        checkMaintenancePage();
    }

    protected void setupMaintenanceRequestHandler() {
        VaadinSession.getCurrent().addRequestHandler((RequestHandler) (s, req, res) -> checkMaintenancePage());
    }

    protected boolean checkMaintenancePage() {
        uiAccessor.accessSynchronously(() -> {
            boolean showMaintenance = false;
            if (Boolean.TRUE.equals(maintenanceConfig.getEnabled())) {
                showMaintenance = !VaadinService.getCurrentRequest()
                        .getParameterMap().containsKey(maintenanceConfig.getLoginParameterName());
            }
            if (showMaintenance) {
                maintenanceBox.removeAll();
                HtmlBoxLayout layout = componentsFactory.createComponent(HtmlBoxLayout.class);
                layout.setTemplateContents(maintenanceConfig.getMaintenancePage());
                layout.setWidth("100%");
                maintenanceBox.add(layout);
            }
            maintenanceBox.setVisible(showMaintenance);
            loginWrapper.setVisible(!showMaintenance);
            poweredByLink.setVisible(!showMaintenance && webConfig.getLoginDialogPoweredByLinkVisible());
        });
        return false;
    }
}