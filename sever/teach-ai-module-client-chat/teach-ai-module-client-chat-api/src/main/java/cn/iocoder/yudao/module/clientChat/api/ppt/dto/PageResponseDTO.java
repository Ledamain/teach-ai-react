package cn.iocoder.teach-ai.module.clientChat.api.ppt.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResponseDTO<T> {
    private List<T> content;        // 当前页的数据内容
    private int totalPages;         // 总页数
    private long totalElements;     // 总元素数量
    private int size;               // 每页大小
    private int number;             // 当前页码（从0开始）
    private boolean first;          // 是否是第一页
    private boolean last;           // 是否是最后一页
    private int numberOfElements;   // 当前页的元素数量
}
