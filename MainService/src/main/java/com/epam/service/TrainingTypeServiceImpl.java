package com.epam.service;

import com.epam.mapper.TrainingTypeMapper;
import com.epam.model.dto.TrainingTypeOutputDto;
import com.epam.repo.TrainingTypeRepo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepo trainingTypeRepo;

    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    @HystrixCommand(commandKey = "getAll", fallbackMethod = "getInitialTrainingTypes")
    public List<TrainingTypeOutputDto> getAll() {
        sleepRandomly();
        return trainingTypeMapper.toDtoList(trainingTypeRepo.findAll());
    }

    //This data is returned if the response from the server is too long (it's simulated by sleepRandomly())
    public List<TrainingTypeOutputDto> getInitialTrainingTypes() {
        List<String> trainingTypes =
                List.of("Strength Training", "Cardiovascular Exercise", "Yoga", "CrossFit", "Pilates", "Martial Arts",
                        "Zumba", "Spinning", "Swimming", "Plyometrics");

        return trainingTypes.stream().map(t -> new TrainingTypeOutputDto(null, t)).toList();
    }

    //This method is needed for demonstration of hystrix work
    private void sleepRandomly() {
        Random random = new Random();
        int randomInt = random.nextInt(3) + 1;

        if (randomInt == 3) {
            System.out.println("It's a chance for demonstrating hystrix action");
            try {
                System.out.println("Start sleeping..." + System.currentTimeMillis());
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Hystrix thread interrupted... " + System.currentTimeMillis());
            }
        }
    }
}