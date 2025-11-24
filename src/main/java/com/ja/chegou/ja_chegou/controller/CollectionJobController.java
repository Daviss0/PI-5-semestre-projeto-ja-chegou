package com.ja.chegou.ja_chegou.controller;

import com.ja.chegou.ja_chegou.enumerated.CollectionStatus;
import com.ja.chegou.ja_chegou.service.CollectionJobService;
import com.ja.chegou.ja_chegou.service.DriverService;
import com.ja.chegou.ja_chegou.service.RouteService;
import com.ja.chegou.ja_chegou.service.TruckService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/collections")
public class CollectionJobController {
    private final CollectionJobService service;
    private final RouteService routeService;
    private final TruckService truckService;
    private final DriverService driverService;

    public CollectionJobController(CollectionJobService service, RouteService routeService, TruckService truckService, DriverService driverService) {
        this.service = service;
        this.routeService = routeService;
        this.truckService = truckService;
        this.driverService = driverService;
    }

    @GetMapping("/status")
    public String statusPage(@RequestParam(required = false)CollectionStatus status,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                             @RequestParam(required = false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
                             @RequestParam(required = false) Long routeId,
                             Model model) {
        LocalDateTime start = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime end = (to != null) ? to.plusDays(1).atStartOfDay() : null;

        model.addAttribute("collections", service.list(status, start, end));
        model.addAttribute("statusValues", CollectionStatus.values());
        model.addAttribute("routes", routeService.findAll());
        model.addAttribute("trucks", truckService.findAll());
        model.addAttribute("drivers", driverService.findAll());
        model.addAttribute("statusFilter", status);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("prefRouteId", routeId);
        return "collections_status";
    }

    @PostMapping("/schedule")
    public String schedule(@RequestParam Long routeId,
                           @RequestParam Long truckId,
                           @RequestParam Long driverId,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime when,
                           @RequestParam(required = false) String notes,
                           RedirectAttributes ra) {
        try {
            service.schedule(routeId, truckId, driverId, when, notes);
            ra.addFlashAttribute("msg", "Coleta agendada com sucesso.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("err", e.getMessage());
            ra.addFlashAttribute("errField", "truckId");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }
        return "redirect:/collections/status";
    }


    @PostMapping("/{id}/start")
    public Object start(@PathVariable Long id,
                        @RequestHeader(value = "X-Requested-With", required = false) String xrw,
                        RedirectAttributes ra) {
        boolean ajax = xrw != null && !xrw.isBlank();
        try {
            service.start(id);
            if (ajax) {
                return ResponseEntity.noContent().build();
            }
            ra.addFlashAttribute("msg", "Coleta #" + id + " iniciada.");
            return "redirect:/collections/status";

        } catch (EntityNotFoundException e) {
            if (ajax) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coleta não encontrada.");
            }
            ra.addFlashAttribute("err", "Coleta não encontrada.");
            return "redirect:/collections/status";

        } catch (IllegalStateException | IllegalArgumentException e) {
            if (ajax) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            ra.addFlashAttribute("err", e.getMessage());
            return "redirect:/collections/status";
        }
    }

    @PostMapping("/{id}/finish")
    public String finish(@PathVariable Long id,
                         @RequestParam(required = false) java.math.BigDecimal weightKg,
                         @RequestParam(required = false) String notes) {
        service.finish(id, weightKg, notes);
        return "redirect:/collections/status";
    }


    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         @RequestParam (required = false) String reason,
                         RedirectAttributes ra) {
        try {
            service.cancel(id, reason);
            ra.addFlashAttribute("msg", "Coleta #" + id + "Cancelada com sucesso.");
        }
        catch (IllegalArgumentException e) {
            ra.addFlashAttribute("err", e.getMessage());
        }
        catch (EntityNotFoundException e) {
            ra.addFlashAttribute("err", "Coleta não encontrada.");
        }
        catch (DataIntegrityViolationException e) {
            ra.addFlashAttribute("err", "Não foi possivel cancelar por restrição de dados");
        }
        catch (TransactionSystemException e) {
            var cause = e.getMostSpecificCause();
            if (cause instanceof jakarta.validation.ConstraintViolationException cve && !cve.getConstraintViolations().isEmpty()) {
                var v = cve.getConstraintViolations().iterator().next();
                String msg = v.getPropertyPath() + " " + v.getMessage();
                ra.addFlashAttribute("err", "Falha de validação: " + msg);
            } else {
                ra.addFlashAttribute("err", "Falha ao salvar alterações.");
            }
        }
        catch(Exception e) {
            ra.addFlashAttribute("err", "Erro inesperado ao cancelar: " + e.getClass().getSimpleName());
        }
        return "redirect:/collections/status";
    }

    private String rootMessage(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null && r.getCause() != r){ r = r.getCause();}
        String message = r.getMessage();
        return message.length() > 200 ? message.substring(0, 200) + "..." : message;
    }
}
