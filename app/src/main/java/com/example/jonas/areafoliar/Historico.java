package com.example.jonas.areafoliar;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Historico extends ExpandableGroup<Folha> {
    public Historico(String title, List<Folha> items) {
        super(title, items);
    }
}
