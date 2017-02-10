package me.machadolucas.diagnosis.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SlashRoot {

    /**
     * Apenas redireciona o usuário do contexto raiz da aplicação para o /app
     *
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "app";
    }

}


