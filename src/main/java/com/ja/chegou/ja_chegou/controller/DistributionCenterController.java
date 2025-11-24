package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.DistributionCenter;
import com.ja.chegou.ja_chegou.service.DistributionCenterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribution-centers")
public class DistributionCenterController {

    private final DistributionCenterService service;

    public DistributionCenterController(DistributionCenterService service) {
        this.service = service;
    }

    @GetMapping
    public List<DistributionCenter> listAll() {
        return service.findAll();
    }

    @PostMapping
    public DistributionCenter create(@RequestBody DistributionCenter center) {
        return service.save(center);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
