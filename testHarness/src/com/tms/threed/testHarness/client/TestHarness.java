package com.tms.threed.testHarness.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.tms.threed.featureModel.shared.Assignment;
import com.tms.threed.featureModel.shared.Bit;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Source;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.picks.Picks;
import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.featureModel.shared.picks.PicksSnapshot;
import com.tms.threed.featurePicker.client.VarPanel;
import com.tms.threed.featurePicker.client.VarPanelFactory;
import com.tms.threed.featurePicker.client.VarPanelModel;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.Jpg;
import com.tms.threed.previewPane.client.PreviewPaneContext;
import com.tms.threed.previewPane.client.externalState.picks.SeriesNotSetException;
import com.tms.threed.previewPane.client.summaryPane.SummaryPaneContext;
import com.tms.threed.previewPane.client.threedServiceClient.ThreedServiceJson;
import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.AngleChangeHandler;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeEvent;
import com.tms.threed.previewPanel.shared.viewModel.ViewChangeHandler;
import com.tms.threed.testHarness.client.services.ThreedServiceRpc;
import com.tms.threed.testHarness.client.services.SeriesKeys;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ViewSnap;
import com.tms.threed.threedModel.client.JsonUnmarshaller;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.gwt.client.dialogs.DialogResources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class TestHarness implements EntryPoint {

    private final ThreedServiceJson threedModelService;

    private final PreviewPaneContext previewPane;
    private final SummaryPaneContext summaryPane;
    private final FeaturePickerPanel featurePickerPanel;
    private final PreviewPanelFrame previewPanelFrame;
    private final MenuBar mainMenuBar;
    private final MenuBar seriesMenuBar = new MenuBar();
    private final MainPanel mainPanel;
    private final LayersPanel layersPanel;

    private SeriesKeys seriesKeys;

    private Map<SeriesKey, ThreedModel> threedModelMap = new HashMap<SeriesKey, ThreedModel>();

    //seriesContext
    private SeriesKey seriesKey;
    private ThreedModel threedModel;
    private FeatureModel featureModel;
    private Picks picks;
    private VarPanelFactory varPanelFactory;
    private VarPanel rootVarPanel;


    public TestHarness() {
        threedModelService = new ThreedServiceJson(new JsonUnmarshaller());

        featurePickerPanel = new FeaturePickerPanel();
        previewPane = new PreviewPaneContext();
        summaryPane = new SummaryPaneContext();
        mainMenuBar = createMenuBar();


        previewPanelFrame = new PreviewPanelFrame(previewPane.getPreviewPanel());

        layersPanel = new LayersPanel(layersPanelModel);

        mainPanel = new MainPanel(previewPanelFrame, layersPanel, featurePickerPanel, mainMenuBar);


        previewPane.addViewChangeHandler(new ViewChangeHandler() {
            @Override public void onChange(ViewChangeEvent e) {
                previewPanelFrame.setViewState(previewPane.getViewState());
            }
        });

        previewPane.addAngleChangeHandler(new AngleChangeHandler() {
            @Override public void onChange(AngleChangeEvent e) {
                previewPanelFrame.setViewState(previewPane.getViewState());
            }
        });


    }

    private ViewSnap getViewState() {
        if (threedModel == null) throw new SeriesNotSetException();
        return previewPane.getViewState();
    }

    private Jpg getCurrentJpg() {
        if (threedModel == null) throw new SeriesNotSetException();
        return threedModel.getJpg(getViewState(), picks);
    }

    private LayersPanelModel layersPanelModel = new LayersPanelModel() {

        @Override public void selectAll() {
            if (threedModel == null) throw new SeriesNotSetException();
            getCurrentJpg().getView().selectAll();
            DeferredCommand.addCommand(new Command() {
                @Override public void execute() {
                    layersPanel.refresh();
                    DeferredCommand.addCommand(new Command() {
                        @Override public void execute() {
                            previewPane.refreshLayerVisibility();
                        }
                    });
                }
            });

        }

        @Override public void selectNone() {
            if (threedModel == null) throw new SeriesNotSetException();
            getCurrentJpg().getView().selectNone();

            DeferredCommand.addCommand(new Command() {
                @Override public void execute() {
                    layersPanel.refresh();
                    DeferredCommand.addCommand(new Command() {
                        @Override public void execute() {
                            previewPane.refreshLayerVisibility();
                        }
                    });
                }
            });
        }

        @Override public List<ImLayer> getLayers() {
            if (threedModel == null) throw new SeriesNotSetException();
            return getCurrentJpg().getView().getLayers();
        }

        @Override public ImPng getPngForLayer(ImLayer layer) {
            if (threedModel == null) throw new SeriesNotSetException();
            return layer.getPng(picks, getViewState().getAngle());
        }

        @Override public void toggleLayer(final ImLayer layer) {
            if (threedModel == null) throw new SeriesNotSetException();
            DeferredCommand.addCommand(new Command() {
                @Override public void execute() {
                    layer.toggleVisibility();
                    previewPane.refreshLayerVisibility();

                }
            });
        }
    };

    public void onModuleLoad() {
        fetchSeriesKeys(new AsyncCallback<List<SeriesKey>>() {
            @Override public void onFailure(Throwable e) {
                Window.alert("Error fetching SeriesKeys: " + e.toString());
                e.printStackTrace();
            }

            @Override public void onSuccess(List<SeriesKey> seriesKeys) {
                TestHarness.this.seriesKeys = new SeriesKeys(seriesKeys);
                populateSeriesMenuBar();
            }
        });

        RootLayoutPanel.get().add(mainPanel);
    }

    private void populateSeriesMenuBar() {
        assert this.seriesKeys != null;
        SortedSet<Integer> years = seriesKeys.getYears();
        for (Integer year : years) {
            MenuBar yearMenuBar = new MenuBar(true);
            SortedSet<SeriesKey> seriesKeys = this.seriesKeys.getSeriesKeys(year);
            for (final SeriesKey seriesKey : seriesKeys) {
                yearMenuBar.addItem(seriesKey.getName(), new Command() {
                    @Override public void execute() {
                        pickSeries(seriesKey);
                    }
                });
            }
            seriesMenuBar.addItem(year + "", yearMenuBar);
        }
    }

    private void pickSeries(final SeriesKey sk) {
        assert sk != null;

        if (this.seriesKey == sk) return;

        mainPanel.showSeriesPanel();

        ThreedModel tdm = threedModelMap.get(sk);
        if (tdm != null) {
            assert tdm.getSeriesKey().equals(sk);
            threedModel = tdm;
            Console.log("\tRefreshAfterThreedModelChange[" + sk + "] ...");
            refreshAfterThreedModelChange();
        } else {
            Console.log("Fetching ThreedModel[" + sk + "] JSON...");
            threedModelService.fetchThreedModel2(sk, new ThreedServiceJson.Callback1() {
                @Override public void onThreeModelReceived(String jsonResponse) {
                    assert jsonResponse != null;
                    Console.log("\tParsing ThreedModel[" + sk + "] JSON...");
                    ThreedModel tdm = threedModelService.parseJsonThreedModel(jsonResponse);
                    assert tdm != null;

                    assert tdm.getSeriesKey().equals(sk);
                    threedModel = tdm;
                    Console.log("\tRefreshAfterThreedModelChange[" + sk + "] ...");
                    refreshAfterThreedModelChange();
                }
            });

        }


    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem(new MenuItem("Pick Series", seriesMenuBar));
        menuBar.addItem(new MenuItem("Gen JPG", genJpgs));
        menuBar.addItem(new MenuItem("JPG Status", showJpgStatus));
        menuBar.addItem(new MenuItem("Picks", showPicksPopup));
        menuBar.addItem(new MenuItem("Summary Panel", showSummaryPopup));
        menuBar.addItem(new MenuItem("Toggle Png Mode", togglePngMode));
        return menuBar;
    }

    private final Command todoCommand = new Command() {
        @Override public void execute() {
            Window.alert("TODO");
        }
    };

    private Command showJpgStatus = new Command() {
        @Override public void execute() {
            String url = "/jpgGenerator/submit.jsp";
            open(url);

//            Window.Location.assign(url);
        }
    };

    private static String jpgGenUrlTemplate = "/jpgGenerator/submit.jsp?seriesName=${seriesName}&seriesYear=${seriesYear}";
    private Command genJpgs = new Command() {

        @Override public void execute() {
            if (seriesKey == null) {
                Window.alert("Please pick a series first.");
            } else {
                String url = jpgGenUrlTemplate.replace("${seriesName}", seriesKey.getName()).replace("${seriesYear}", seriesKey.getYear() + "");
                open(url);
            }
        }

    };

    public static native void open(String url) /*-{
        $wnd.open(url);
    }-*/;

    private Command showPicksPopup = new Command() {

        @Override public void execute() {
            if (seriesKey == null) {
                Window.alert("Please pick a series first.");
            } else {
                PicksPopup picksPopup = new PicksPopup();
                picksPopup.center();
                picksPopup.show();
            }
        }

    };

    private Command showSummaryPopup = new Command() {

        @Override public void execute() {
            if (seriesKey == null) {
                Window.alert("Please pick a series first.");
            } else {

                summaryPane.setSeries(threedModel);
                summaryPane.setPicks(new PicksChangeEvent(null, picks.createSnapshot(),null));

                DialogBox popup = new MyDialogBox("Summary");
                popup.setAnimationEnabled(true);


                popup.setWidget(summaryPane.getSummaryPanel());

                popup.center();
                popup.show();


            }
        }

    };

    static class MyDialogBox extends DialogBox {

        //        int dialogWidth = TopImagePanel.PREFERRED_WIDTH_PX;
        private static final int WIDTH_OF_CLOSE_BUTTON_PX = 27;

        MyDialogBox(String titleBar) {
            super(true);
            String url = DialogResources.INSTANCE.closeButton().getURL();
            setHTML("<table style='margin:0;padding:0' width='100%'><tr><td><b>" + titleBar + "</b></td><td align='right'><img src='" + url + "'/></td></tr></table>");
        }

        @Override
        protected void beginDragging(MouseDownEvent ev) {
            int dialogWidth = this.getOffsetWidth();

            int L = dialogWidth - WIDTH_OF_CLOSE_BUTTON_PX - 20;
            int R = dialogWidth;

            int x = ev.getX();
            int y = ev.getY();
            if (x > L && x < R && y > 5 && y < 33) {
                this.hide();
            } else {
                super.beginDragging(ev);
            }
        }
    }

    private boolean pngMode = false;

    private Command togglePngMode = new Command() {

        @Override public void execute() {
            if (seriesKey == null) {
                Window.alert("Please pick a series first.");
            } else {
                pngMode = !pngMode;
                previewPane.setPngMode(pngMode);
                previewPanelFrame.setPngMode(pngMode);

            }
        }

    };

    private class PicksPopup extends MyDialogBox {

        FlexTable t = new FlexTable();

        private PicksPopup() {
            super("Picks");
            this.setAnimationEnabled(true);
            int varCount = featureModel.getVarCount();
            for (int varIndex = 0; varIndex < varCount; varIndex++) {
                Var var = featureModel.getVar(varIndex);
                Assignment assignment = picks.getAssignment(var);
                Bit value = assignment.getValue();
                Source source = assignment.getSource();
                int row = varIndex;
                t.setText(row, 0, var.getCode());
                t.setText(row, 1, var.getName());
                t.setText(row, 2, value.toString());
                t.setText(row, 3, source.toString());
            }

            t.getColumnFormatter().setWidth(0, "200px");
            t.getColumnFormatter().setWidth(1, "250px");
            t.getColumnFormatter().setWidth(2, "100px");
            t.getColumnFormatter().setWidth(3, "100px");

            ScrollPanel scrollPanel = new ScrollPanel(t);
            scrollPanel.setHeight("20em");
//            scrollPanel.setWidth("700px");
            setWidget(scrollPanel);


        }
    }

    private void fetchSeriesKeys(AsyncCallback<List<SeriesKey>> callback) {
        ThreedServiceRpc.App.getInstance().fetchSeriesKeys(callback);
    }

    private void refreshAfterThreedModelChange() {
        assert threedModel != null;

        threedModelMap.put(threedModel.getSeriesKey(), threedModel);
        seriesKey = threedModel.getSeriesKey();
        featureModel = threedModel.getFeatureModel();

        previewPane.setSeries(threedModel);

        if (seriesKey.isa(SeriesKey.CAMRY)) {
            previewPane.setMsrp("$33,333");
        } else if (seriesKey.isa(SeriesKey.VENZA)) {
            previewPane.setMsrp("$22,222");
            previewPane.setChatInfo(new ChatInfo());
        } else if (seriesKey.isa(SeriesKey.AVALON)) {
            previewPane.setMsrp("$44,444");
        } else if (seriesKey.isa(SeriesKey.YARIS)) {
            previewPane.setMsrp("$11,1111");
        } else {
            previewPane.setMsrp("$12,345");
            previewPane.setChatInfo(null);
        }


        picks = featureModel.getInitialVisiblePicks();
        picks.fixup();

        assert picks.isValid();

        previewPane.setMsrp("$11,111");
        previewPane.setChatInfo(null);

        PicksSnapshot picksSnap = picks.createSnapshot();
        assert picksSnap.isValid();

        previewPane.setPicks(new PicksChangeEvent(null, picksSnap,null));

        varPanelFactory = new VarPanelFactory();
        varPanelFactory.setVarPanelContext(new MyVarPanelModel());
        rootVarPanel = varPanelFactory.createVarPanel(featureModel.getRootVar());

        picks.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                previewPane.setPicks(e);
                rootVarPanel.refresh();
            }
        });

        featurePickerPanel.setVarPanel(rootVarPanel);
        rootVarPanel.refresh();
        layersPanel.refresh();

        previewPanelFrame.setPngMode(pngMode);
        previewPanelFrame.setViewState(threedModel.getInitialViewState());

    }

    class MyVarPanelModel implements VarPanelModel {
        @Override public boolean showFieldHeadings() {
            return true;
        }

        @Override public boolean hideDerived() {
            return true;
        }

        @Override public VarPanel getVarPanel(Var var) {
            if (threedModel == null) throw new SeriesNotSetException();
            return varPanelFactory.getVarPanel(var);
        }

        @Override public Picks getPicks() {
            if (threedModel == null) throw new SeriesNotSetException();
            return picks;
        }
    }


}
