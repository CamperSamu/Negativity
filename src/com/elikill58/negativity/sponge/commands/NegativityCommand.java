package com.elikill58.negativity.sponge.commands;

import static org.spongepowered.api.command.args.GenericArguments.requiringPermission;
import static org.spongepowered.api.command.args.GenericArguments.user;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.sponge.Messages;
import com.elikill58.negativity.sponge.commands.child.AdminCommand;
import com.elikill58.negativity.sponge.commands.child.AlertCommand;
import com.elikill58.negativity.sponge.commands.child.ClearCommand;
import com.elikill58.negativity.sponge.commands.child.ModCommand;
import com.elikill58.negativity.sponge.commands.child.ReloadCommand;
import com.elikill58.negativity.sponge.commands.child.VerifCommand;
import com.elikill58.negativity.sponge.inventories.AbstractInventory;
import com.elikill58.negativity.sponge.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.sponge.utils.NegativityCmdSuggestionsEnhancer;
import com.elikill58.negativity.sponge.utils.NegativityCmdWrapper;

public class NegativityCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Messages.getMessage(src, "only_player"));
		}

		Player playerSource = ((Player) src);
		Optional<User> optUser = args.getOne("target");
		if (optUser.isPresent()) {
			User user = optUser.get();
			Optional<Player> optPlayer = user.getPlayer();
			if(optPlayer.isPresent())
				AbstractInventory.open(InventoryType.CHECK_MENU, playerSource, optPlayer.get());
			else
				AbstractInventory.open(InventoryType.CHECK_MENU_OFFLINE, playerSource, user);
			return CommandResult.success();
		}
		Messages.sendMessageList(playerSource, "negativity.verif.help");
		return CommandResult.empty();
	}
	
	public Optional<User> getUser(String name) {
	    Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
	    return userStorage.get().get(name);
	}

	public static CommandCallable create() {
		// To work around an undesirable behaviour of arguments completion,
		// we wrap /negativity in a CommandCallable that always suggests online players
		// in addition to the default suggestion results.
		NegativityCmdSuggestionsEnhancer command = new NegativityCmdSuggestionsEnhancer(CommandSpec.builder()
				.executor(new NegativityCommand())
				.arguments(requiringPermission(user(Text.of("target")), "negativity.verif"))
				.child(VerifCommand.create(), "verif")
				.child(ClearCommand.create(), "clear")
				.child(AlertCommand.create(), "alert")
				.child(ModCommand.create(), "mod")
				.child(AdminCommand.create(), "admin")
				.child(ReloadCommand.create(), "reload")
				.build());
		return new NegativityCmdWrapper(command, false, null);
	}
}
