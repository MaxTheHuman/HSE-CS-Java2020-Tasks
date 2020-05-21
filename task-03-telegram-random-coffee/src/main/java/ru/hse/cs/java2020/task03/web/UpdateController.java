package ru.hse.cs.java2020.task03.web;


import ru.hse.cs.java2020.task03.core.Update;
import ru.hse.cs.java2020.task03.core.UpdateHandler;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("api")
public class UpdateController {

    @Inject
    private UpdateHandler updateHandler;

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void processUpdate(Update update) {
        updateHandler.onUpdate(update);
    }
}
