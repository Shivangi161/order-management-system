package com.intuit.ordermanagementservice.DTOs;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProductDTOs {

    @NotNull
    @Size(min = 1)
    private List<ProductDTO> productDTOList;


}
