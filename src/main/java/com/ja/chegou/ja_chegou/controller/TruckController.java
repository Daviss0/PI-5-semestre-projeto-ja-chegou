package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.entity.Truck;
import com.ja.chegou.ja_chegou.service.DriverService;
import com.ja.chegou.ja_chegou.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/truck")
public class TruckController {

    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    @Autowired
    DriverService driverService;

    @GetMapping("/menu")
    public String menu() {
        return "menu_truck";
    }

    @GetMapping("/list")
    public String listTrucks(Model model) {
        model.addAttribute("trucks", truckService.findAll());
        return "truck_list";
    }

    @GetMapping("/add")
    public String showAddTruckForm(Model model) {
        model.addAttribute("truck", new Truck());
        return "truck_form";
    }

    @PostMapping("/add")
    public String addTruck(@ModelAttribute Truck truck) {
        truckService.save(truck);
        return "redirect:/truck/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditTruckForm(@PathVariable Long id, Model model) {
        model.addAttribute("truck", truckService.findById(id));
        return "truck_form";
    }

    @PostMapping("/edit/{id}")
    public String updateTruck(@PathVariable Long id, @ModelAttribute Truck truck) {
        truckService.update(truck);
        return "redirect:/truck/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTruck(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            truckService.delete(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/truck/list";
    }

    @GetMapping("/assign")
    public String showAssignDriverForm(Model model) {
        model.addAttribute("trucks", truckService.findAll());
        model.addAttribute("drivers", driverService.findAllAvailable());
        return "truck_assign";
    }

    @PostMapping("/assign")
    public String assignDriver(@RequestParam Long truckId, @RequestParam Long driverId) {
        truckService.assignDriver(truckId, driverId);
        return "redirect:/truck/list";
    }

    @GetMapping("/details/{id}")
    public String showTruckDetails(@PathVariable Long id, Model model) {
        Truck truck = truckService.findById(id);
        model.addAttribute("truck", truck);
        return "truck_details";
    }

    @GetMapping("reports")
    public String showReports(Model model) {
        model.addAttribute("trucksByDriver", truckService.getTruckByDriver());
        model.addAttribute("truckStatus", truckService.getTruckStatusSummary());
        model.addAttribute("capacityPerRoute", truckService.getTotalCapacityPerRoute());
        return "truck_reports";
    }

    @RestController
    @RequestMapping("/api/trucks/public")
    static class TruckPublicController {
        private final TruckService truckService;

        public TruckPublicController(TruckService truckService) {
            this.truckService = truckService;
        }

        @GetMapping("/live")
        public List<TruckMapDTO> listDriverTrucks() {
            return truckService.findAll().stream()
                    .map(TruckMapDTO::from)
                    .collect(Collectors.toList());
        }

        public record TruckMapDTO(Long id, String plate, double lat, double lng, Long routeId) {
            public static TruckMapDTO from(Truck t) {
                double lat = 0;
                double lng = 0;
                Long routeId = null;

                if (t.getCurrentLatitude() != null && t.getCurrentLongitude() != null) {
                    lat = t.getCurrentLatitude();
                    lng = t.getCurrentLongitude();
                }

                else if (t.getRoute() != null) {
                    lat = t.getRoute().getDestinationLatitude();
                    lng = t.getRoute().getDestinationLongitude();
                    routeId = t.getRoute().getId();
                }

                return new TruckMapDTO(
                        t.getId(),
                        t.getPlate(),
                        lat,
                        lng,
                        routeId
                );
            }
            }
    }

}
