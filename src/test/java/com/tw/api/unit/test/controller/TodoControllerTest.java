package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {


    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    List<Todo> todos;

    @BeforeEach
    void initialTodos() {
        todos = Arrays.asList(new Todo[]{new Todo("todo item 1", false)});
    }

    @Test
    void getAll() throws Exception {
        //given
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(0)))
                .andExpect(jsonPath("$[0].title", is("todo item 1")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[0].order", is(0)));
    }

    @Test
    void getTodo() throws Exception {
        Todo todo = new Todo("todo item 1", false);
        //given
        when(todoRepository.findById(0)).thenReturn(java.util.Optional.of(todo));
        //when
        ResultActions result = mvc.perform(get("/todos/0"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.title", is("todo item 1")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.order", is(0)));
    }

    @Test
    void saveTodo() throws Exception {
        Todo todo = new Todo("todo item 1", false);
        String item = new ObjectMapper().writeValueAsString(new Todo("todo item 2", false));
        //given
        when(todoRepository.getAll()).thenReturn(todos);

        //when
        ResultActions result = mvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(item));
        //then
        result.andExpect(status().isCreated());
    }

    @Test
    void deleteOneTodo() throws Exception {
        Todo todo = new Todo("todo item 1", false);
        //given
        when(todoRepository.findById(0)).thenReturn(java.util.Optional.of(todo));

        //when
        ResultActions result = mvc.perform(delete("/todos/0")
                .content(String.valueOf(todo)));
        //then
        result.andExpect(status().isOk());
    }

    @Test
    void updateTodo() throws Exception {
        Todo todo = new Todo("todo item 1", false);
        //given
        when(todoRepository.findById(0)).thenReturn(java.util.Optional.of(todo));

        //when
        ResultActions result = mvc.perform(patch("/todos/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new Todo("todo item 2", false))));
        //then
        result.andExpect(status().isOk());
    }
}