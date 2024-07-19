package com.paddy.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ChatController {
    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }
    @GetMapping("/hello")
    public String Hello(@RequestParam(value = "message", defaultValue = "Hello AI, How are you") String message) {
        return this.chatClient.prompt().user(message)
                .call().content();
    }
    @GetMapping("/prompt")
    public String simplePrompt(@RequestParam(value = "message", defaultValue = "Hello AI, How are you") String message) {
        return this.chatClient.prompt(new Prompt(message))
                .call().content();
    }
    @GetMapping("/popular")
    public String getHistoryMovie(@RequestParam(value = "category", defaultValue = "悬疑") String category) {
        String message = """
                列出你认为世界上最好看的十部{category}电影，带上他们上映的年份;
                以 JSON 格式返回结果，并只返回结果，不返回任何不用的描述，如果返回其他信息会受到处罚
                """;

        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("category", category));
        return this.chatClient.prompt(prompt)
                .call().content();
    }
    @GetMapping("/master")
    public String master(@RequestParam(value = "message") String message) {
        var system = new SystemMessage("""
                你是一个算命大师，名叫陈玉楼，负责为客户解答命理相关的内容；
                你从不说自己是一个人工智能助手或AI，而是以老夫、老朽等自称。
                你需要依赖用户的出生年月来进行算命，当初和用户对话的时候，你会先问用户的姓名和出生年月日，以便以后使用。
                以下是你常说的一些口头禅：
                1. “命里有时终须有，命里无时莫强求。”
                2. “山穷水复疑无路，柳暗花明又一村。”
                """);
        // 系统的设定和提问给openai
        Prompt prompt = new Prompt(List.of(system, new UserMessage(message)));
        return this.chatClient.prompt(prompt)
                .call().content();
    }
}
