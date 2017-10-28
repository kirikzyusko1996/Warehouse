package com.itechart.warehouse.dto.forecasting;

import lombok.*;

/**
 * Created by Lenovo on 07.10.2017.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDTO {
    private Integer price;
    private Integer daysUntilRelease;
    private Integer daysKeeping;
    private Category category;
}
