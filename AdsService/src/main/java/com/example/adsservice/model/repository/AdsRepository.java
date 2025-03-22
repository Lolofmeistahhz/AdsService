package com.example.adsservice.model.repository;

import com.example.adsservice.model.entity.Ads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Integer> {
}