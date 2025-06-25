package com.hg.batch.chunk;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.List;

public class ListItemReader implements ItemReader<Integer> {

    private final List<Integer> list;

    public ListItemReader(List<Integer> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public Integer read() throws UnexpectedInputException, ParseException, NonTransientResourceException {
        Integer element;
        if (list.isEmpty()) {
            element = null;
        } else {
            element = list.remove(0);
        }
        System.out.println("읽어온 element : " + element);
        return element;
    }
}
