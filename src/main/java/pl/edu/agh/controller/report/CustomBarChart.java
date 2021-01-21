package pl.edu.agh.controller.report;

import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class CustomBarChart<String, Double> extends BarChart<String, Double> {

    private final Map<Node, TextFlow> nodeMap = new HashMap<>();

    @Setter
    private Axis<String> xAxis;
    @Setter
    private Axis<Double> yAxis;

    public CustomBarChart() {
        super((Axis<String>) new CategoryAxis(), (Axis<Double>) new NumberAxis());
    }

    public CustomBarChart(Axis<String> xAxis, Axis<Double> yAxis) {
        super(xAxis, yAxis);
        this.setBarGap(0.0);
    }

    private static final class CustomBarChartBuilder<X, Y> {
        private Axis<X> xAx;
        private Axis<Y> yAx;

        public CustomBarChartBuilder<X, Y> xAxis(Axis<X> xAxis) {
            this.xAx = xAxis;
            return this;
        }

        public CustomBarChartBuilder<X, Y> yAxis(Axis<Y> yAxis) {
            this.yAx = yAxis;
            return this;
        }

        public CustomBarChart<X, Y> build() {
            CustomBarChart<X, Y> bar = new CustomBarChart<>(xAx, yAx);
            return bar;
        }
    }

    @Override
    protected void seriesAdded(Series<String, Double> series, int seriesIndex) {

        super.seriesAdded(series, seriesIndex);

        for (int j = 0; j < series.getData().size(); j++) {

            Data<String, Double> item = series.getData().get(j);

            Text text = new Text(item.getYValue().toString());
            text.setStyle("-fx-font-size: 10pt;");

            TextFlow textFlow = new TextFlow(text);
            textFlow.setTextAlignment(TextAlignment.CENTER);

            nodeMap.put(item.getNode(), textFlow);
            this.getPlotChildren().add(textFlow);

        }

    }

    @Override
    protected void seriesRemoved(final Series<String, Double> series) {

        for (Node bar : nodeMap.keySet()) {

            Node text = nodeMap.get(bar);
            this.getPlotChildren().remove(text);

        }

        nodeMap.clear();

        super.seriesRemoved(series);
    }

    @Override
    protected void layoutPlotChildren() {

        super.layoutPlotChildren();

        for (Node bar : nodeMap.keySet()) {

            TextFlow textFlow = nodeMap.get(bar);

            if (bar.getBoundsInParent().getHeight() > 30) {
                ((Text) textFlow.getChildren().get(0)).setFill(Color.WHITE);
                textFlow.resize(bar.getBoundsInParent().getWidth(), 200);
                textFlow.relocate(bar.getBoundsInParent().getMinX(), bar.getBoundsInParent().getMinY() + 10);
            } else {
                ((Text) textFlow.getChildren().get(0)).setFill(Color.GRAY);
                textFlow.resize(bar.getBoundsInParent().getWidth(), 200);
                textFlow.relocate(bar.getBoundsInParent().getMinX(), bar.getBoundsInParent().getMinY() - 20);
            }
        }
    }
}
