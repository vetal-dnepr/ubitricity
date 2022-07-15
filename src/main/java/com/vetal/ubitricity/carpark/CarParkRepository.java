package com.vetal.ubitricity.carpark;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarParkRepository extends CrudRepository<CarPark, Integer> {

}