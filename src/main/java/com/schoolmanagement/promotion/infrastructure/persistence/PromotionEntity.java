package com.schoolmanagement.promotion.infrastructure.persistence;

import java.util.UUID;

import com.schoolmanagement.promotion.domain.PromotionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PromotionStatus status;

  @Version
  @Column(nullable = false)
  private long version;

  protected PromotionEntity() {}

  public PromotionEntity(
      UUID id,
      String name,
      String academicYear,
      int capacity,
      int occupancy,
      PromotionStatus status,
      long version) {
    this.id = id;
    this.name = name;
    this.academicYear = academicYear;
    this.capacity = capacity;
    this.occupancy = occupancy;
    this.status = status;
    this.version = version;
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

  public PromotionStatus getStatus() {
    return status;
  }

  public long getVersion() {
    return version;
  }
}
