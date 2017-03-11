package com.lcz.file.excel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lcz.file.entity.CellEntity;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/19.
 */
public class SheetDataHandler extends RowMapper {
    private int bufRowSize, curSheetIndex = -1;
    private List<List<Map<Integer, Object>>> sheetMapDataList = Lists.newArrayList();
    private List<Map<Integer, Object>> sheetMapData;


    public List<List<Map<Integer, Object>>> getSheetMapDataList() {
        return sheetMapDataList;
    }


    public SheetDataHandler(int bufRowSize) {
        this.bufRowSize = bufRowSize;
    }

    @Override
    void mapRow(int sheetIndex, int rowIndex, List<CellEntity> row) {
        if (curSheetIndex != sheetIndex) {
            sheetMapData = new ArrayList<>(sheetIndex == 0 ? bufRowSize : sheetMapData.size() / 2);
            sheetMapDataList.add(sheetMapData);
            curSheetIndex = sheetIndex;
        }
        Map<Integer, Object> map = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(row)) {
            row.stream().filter(cellEntity -> cellEntity.getCellIndex() != null).forEach(cellEntity -> {
                map.put(cellEntity.getCellIndex(), cellEntity.getObject());
            });
        }
        sheetMapData.add(map);
    }
}
