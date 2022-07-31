package de.ritter.deconz.repository;

import de.ritter.deconz.api.Sensors;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRepository extends MongoRepository<Sensors, String> {
}
