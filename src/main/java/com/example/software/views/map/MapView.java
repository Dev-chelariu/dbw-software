package com.example.software.views.map;

import com.example.software.data.entity.dto.LocationDTO;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.FeatureLayer;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import javax.annotation.security.PermitAll;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Location")
@Route(value = "map", layout = MainLayout.class)
@PermitAll
public class MapView extends HorizontalLayout {

    private static LocationDTO[] locationDTOS = new LocationDTO[]{
            new LocationDTO(1, "Romania", "Iasi", "DBW - WAREHOUSE", 47.18217365544058, 27.474595343290996),
            new LocationDTO(2, "Andorra", "Andorra la Vella", "Casa de la Vall", 42.506563, 1.520563),
            new LocationDTO(3, "Greece", "Athens", "Acropolis of Athens", 37.971563, 23.725687),
            new LocationDTO(4, "Serbia", "Belgrade", "Belgrade Fortress", 44.823062, 20.450688),
            new LocationDTO(5, "Germany", "Berlin", "Brandenburg Gate", 52.516312, 13.377688),
            new LocationDTO(6, "Switzerland", "Bern", "Bern Old Town", 46.948187, 7.450188),
            new LocationDTO(7, "Slovakia", "Bratislava", "Bratislava Castle", 48.142063, 17.100187),
            new LocationDTO(8, "Belgium", "Brussels", "Grand Place", 50.846812, 4.352438),
            new LocationDTO(9, "Romania", "Bucharest", "Romanian Athenaeum", 44.441312, 26.097313),
            new LocationDTO(10, "Hungary", "Budapest", "Fisherman's Bastion", 47.502187, 19.034813)};

    private Map map = new Map();

    private UnorderedList cardList;
    private java.util.Map<LocationDTO, Button> locationToCard = new HashMap<>();

    private List<LocationDTO> filteredLocationDTOS;
    private java.util.Map<Feature, LocationDTO> featureToLocation = new HashMap<>();

    public MapView() {
        addClassName("map-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        map.getElement().setAttribute("theme", "borderless");
        map.getElement().setAttribute("class", "map");
        map.setHeightFull();

        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setSpacing(false);
        sidebar.setPadding(false);

        sidebar.setWidth("auto");
        sidebar.addClassNames("sidebar");
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search");
        searchField.setWidthFull();
        searchField.addClassNames(Padding.MEDIUM, BoxSizing.BORDER);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            updateFilter(searchField.getValue().toLowerCase());
        });
        searchField.setClearButtonVisible(true);
        searchField.setSuffixComponent(new Icon("lumo", "search"));

        Scroller scroller = new Scroller();
        scroller.addClassNames(Padding.Horizontal.MEDIUM, Width.FULL, BoxSizing.BORDER);

        cardList = new UnorderedList();
        cardList.addClassNames("card-list", Gap.XSMALL, Display.FLEX, FlexDirection.COLUMN, ListStyleType.NONE,
                Margin.NONE, Padding.NONE);
        sidebar.add(searchField, scroller);
        scroller.setContent(cardList);

        add(map, sidebar);

        configureMap();
        updateCardList();
    }

    private void centerMapOn(LocationDTO locationDTO) {
        View view = map.getView();
        view.setCenter(new Coordinate(locationDTO.getLongitude(), locationDTO.getLatitude()));
        view.setZoom(14);
    }

    private void scrollToCard(LocationDTO locationDTO) {
        locationToCard.get(locationDTO).scrollIntoView();
    }

    private void centerMapDefault() {
        View view = new View();
        view.setCenter(new Coordinate(7, 55));
        view.setZoom(4.4f);
        map.setView(view);
    }

    private void configureMap() {

        this.centerMapDefault();

        this.map.addFeatureClickListener(e -> {
            Feature feature = e.getFeature();
            LocationDTO locationDTO = featureToLocation.get(feature);
            this.centerMapOn(locationDTO);
            this.scrollToCard(locationDTO);
        });

        this.updateFilter("");
    }

    private void updateCardList() {
        cardList.removeAll();
        locationToCard.clear();
        for (LocationDTO locationDTO : filteredLocationDTOS) {
            Button button = new Button();
            button.addClassNames(Height.AUTO, Padding.MEDIUM);
            button.addClickListener(e -> {
                centerMapOn(locationDTO);
            });

            Span card = new Span();
            card.addClassNames("card", Width.FULL, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Gap.XSMALL);
            Span country = new Span(locationDTO.getCountry());
            country.addClassNames(TextColor.SECONDARY);
            Span city = new Span(locationDTO.getCity());
            city.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, TextColor.HEADER, Padding.Bottom.XSMALL);
            Span place = new Span(locationDTO.getPlace());
            place.addClassNames(TextColor.SECONDARY);

            card.add(country, city, place);

            button.getElement().appendChild(card.getElement());
            cardList.add(new ListItem(button));
            locationToCard.put(locationDTO, button);
        }
    }

    private void updateFilter(String filter) {
        featureToLocation.clear();
        filteredLocationDTOS = Stream.of(locationDTOS)
                .filter(locationDTO -> locationDTO.getPlace().toLowerCase().contains(filter)
                        || locationDTO.getCity().toLowerCase().contains(filter)
                        || locationDTO.getCountry().toLowerCase().contains(filter))
                .collect(Collectors.toList());

        FeatureLayer featureLayer = this.map.getFeatureLayer();

        for (Feature f : featureLayer.getFeatures().toArray(Feature[]::new)) {
            featureLayer.removeFeature(f);
        }

        this.filteredLocationDTOS.forEach((locationDTO) -> {
            MarkerFeature feature = new MarkerFeature(new Coordinate(locationDTO.getLongitude(), locationDTO.getLatitude()));
            featureToLocation.put(feature, locationDTO);
            featureLayer.addFeature(feature);
        });
        updateCardList();
    }
}
