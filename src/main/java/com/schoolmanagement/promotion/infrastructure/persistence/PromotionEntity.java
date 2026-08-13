package com.schoolmanagement.promotion.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotions")
public class PromotionEntity {

  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(name = "academic_year", nullable = false)
  private String academicYear;

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false)
  private int occupancy;

  protected PromotionEntity() {}

  public PromotionEntity(
      UUID id,
      String name,
      String academicYear,
      int capacity,
      int occupancy) {
    this.id = id;
    this.name = name;
    this.academicYear = academicYear;
    this.capacity = capacity;
    this.occupancy = occupancy;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAcademicYear() {
    return academicYear;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getOccupancy() {
    return occupancy;
  }
}
