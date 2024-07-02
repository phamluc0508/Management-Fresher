package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Center;
import com.vmo.management_fresher.service.CenterService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/center")
@RequiredArgsConstructor
@Tag(name = "Center", description = "Center API ")
public class CenterApi {
    private final CenterService service;

    @PostMapping()
    @Operation(summary = "Create single Center"
            , description = "Create single Center"
            , tags = {"Center"}
    )
    protected ResponseEntity createCenter(
            @RequestHeader String uid,
            @RequestBody Center request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.createCenter(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Center by Id"
            , description = "Update single Center, path param is Center"
            , tags = {"Center"}
    )
    protected ResponseEntity updateCenter(
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody Center request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.updateCenter(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Center by Id"
            , description = "Delete single Center, path param is Center"
            , tags = {"Center"}
    )
    protected ResponseEntity deleteCenter(
            @PathVariable("id") Long id
    ){
        try {
            return ResponseUtils.handlerSuccess(service.deleteCenter(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "View Center by Id "
            , description = "View single Center, path param is centerId"
            , tags = { "Center" }
    )
    protected ResponseEntity getById(
            @PathVariable("id") Long id
    ){
        try {
            return ResponseUtils.handlerSuccess(service.getById(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    @Operation(summary = "View all Center"
            , description = "View all Center"
            , tags = { "Center" }
    )
    protected ResponseEntity getAll(){
        try {
            return ResponseUtils.handlerSuccess(service.getAll());
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search all Center with name "
            , description = "Search all Center "
            , tags = { "Center"  }
    )
    protected ResponseEntity search(
            @RequestParam(value = "search", defaultValue = "#") String search,
            Pageable pageable
    ){
        try {
            return ResponseUtils.handlerSuccess(service.search(search, pageable));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
