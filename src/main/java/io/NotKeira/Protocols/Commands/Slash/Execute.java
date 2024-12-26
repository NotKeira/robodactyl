package io.NotKeira.Protocols.Commands.Slash;

import java.io.IOException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.NotKeira.Config.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Execute implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Configuration config = new Configuration();
        String host = config.getString("pterodactyl.host");
        String apiKey = config.getString("pterodactyl.key");
        String serverId = config.getString("pterodactyl.server");
        String command = Objects.requireNonNull(event.getOption("command")).getAsString();

        EmbedBuilder embed = new EmbedBuilder().setDescription("Executing command...").setColor(0x2b2d31).setFooter("Powered by RoboDactyl");
        event.replyEmbeds(embed.build()).queue();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(host + "/api/client/servers/" + serverId + "/command").post(RequestBody.create("{\"command\": \"" + command + "\"}", MediaType.parse("application/json"))).addHeader("Authorization", "Bearer " + apiKey).addHeader("Content-Type", "application/json").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                EmbedBuilder embed = new EmbedBuilder().setDescription("Failed to execute command: **" + e.getMessage() + "**").setColor(0x2b2d31).setFooter("Powered by RoboDactyl");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    EmbedBuilder embed = new EmbedBuilder().setDescription("Successfully executed command on server.").setColor(0x2b2d31).setFooter("Powered by RoboDactyl");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                } else {
                    EmbedBuilder embed = new EmbedBuilder().setDescription("Failed to execute command: **" + response.message() + "**").setColor(0x2b2d31).setFooter("Powered by RoboDactyl");
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            }
        });
    }
}