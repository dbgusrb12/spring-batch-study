package com.hg.batch.chunk;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

public class ListItemWriter implements ItemWriter<String> {

    private final List<String> writtenItems = new ArrayList<>();

    @Override
    public void write(Chunk<? extends String> chunk) {
        List<? extends String> items = chunk.getItems();
        System.out.println("쓸 element : " + items);
        writtenItems.addAll(items);
    }
}
