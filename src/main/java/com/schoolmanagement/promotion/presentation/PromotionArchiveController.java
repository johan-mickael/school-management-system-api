package com.schoolmanagement.promotion.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.promotion.application.query.getarchivedpromotion.GetArchivedPromotionHandler;
import com.schoolmanagement.promotion.application.query.getarchivedpromotion.GetArchivedPromotionQuery;
import com.schoolmanagement.promotion.application.query.listarchivedpromotions.ListArchivedPromotionsHandler;
import com.schoolmanagement.promotion.application.query.listarchivedpromotions.ListArchivedPromotionsQuery;
import com.schoolmanagement.promotion.presentation.dto.PromotionResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/archive/promotions")
@Tag(name = "Promotions")
public class PromotionArchiveController {

  private final ListArchivedPromotionsHandler list;
  private final GetArchivedPromotionHandler getOne;

  public PromotionArchiveController(ListArchivedPromotionsHandler list, GetArchivedPromotionHandler getOne) {
    this.list = list;
    this.getOne = getOne;
  }

  @GetMapping
  public List<PromotionResponse> list() {
    return list.handle(new ListArchivedPromotionsQuery()).stream()
        .map(PromotionResponse::from)
        .toList();
  }

  @GetMapping("/{id}")
  public PromotionResponse getOne(@PathVariable String id) {
    return PromotionResponse.from(getOne.handle(new GetArchivedPromotionQuery(id)));
  }
}
