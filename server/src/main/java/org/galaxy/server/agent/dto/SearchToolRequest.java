package org.galaxy.server.agent.dto;

import lombok.Data;

@Data
public class SearchToolRequest {
    private String name;
    private Integer rating;
    private Integer distance;
    private Integer price;
    private String cuisine;
    private Integer limit;
}
