package fr.Alphart.BAT.Modules.Kick;

import static com.google.common.base.Preconditions.checkArgument;
import static fr.Alphart.BAT.I18n.I18n.formatWithColor;

import java.util.UUID;

import fr.Alphart.BAT.I18n.I18n;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;


import fr.Alphart.BAT.BAT;
import fr.Alphart.BAT.Modules.BATCommand;
import fr.Alphart.BAT.Modules.BATCommand.RunAsync;
import fr.Alphart.BAT.Modules.CommandHandler;
import fr.Alphart.BAT.Modules.IModule;
import fr.Alphart.BAT.Modules.InvalidModuleException;
import fr.Alphart.BAT.Modules.Core.PermissionManager;
import fr.Alphart.BAT.Modules.Core.PermissionManager.Action;
import fr.Alphart.BAT.Utils.FormatUtils;
import fr.Alphart.BAT.Utils.Utils;

public class KickCommand extends CommandHandler {
	private static Kick kick;

	public KickCommand(final Kick kickModule) {
		super(kickModule);
		kick = kickModule;
	}

	@RunAsync
	public static class KickCmd extends BATCommand {
		public KickCmd() {
			super("kick", "<player> [reason]", "Kick the player from his current server to the lobby", Action.KICK
					.getPermission());
		}

		@Override
		public void onCommand(final CommandSender sender, final String[] args, final boolean confirmedCmd)
				throws IllegalArgumentException {
			if (args[0].equals("help")) {
				try {
					FormatUtils.showFormattedHelp(BAT.getInstance().getModules().getModule("kick").getCommands(),
							sender, "KICK");
				} catch (final InvalidModuleException e) {
					e.printStackTrace();
				}
				return;
			}
                        
            checkArgument(args.length != 1 || !BAT.getInstance().getConfiguration().isMustGiveReason(),
                I18n.formatWithColor("noReasonInCommand"));
                        
			final String pName = args[0];
	    	final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pName);
	    	// The player is online on the proxy
	    	if(player != null){
	    		final String pServer = player.getServer().getInfo().getName();
   				checkArgument(
					pServer != null && !pServer.equals(player.getPendingConnection().getListener().getDefaultServer()),
					formatWithColor("cantKickDefaultServer", new String[] { pName }));

   				checkArgument(
					PermissionManager.canExecuteAction(Action.KICK, sender, player.getServer().getInfo().getName()),
					I18n.formatWithColor("noPerm"));

   				checkArgument(!PermissionManager.isExemptFrom(Action.KICK, pName), I18n.formatWithColor("isExempt"));

   				final String returnedMsg = kick.kick(player, sender.getName(),
					(args.length == 1) ? IModule.NO_REASON : Utils.getFinalArg(args, 1));
   				BAT.broadcast(returnedMsg, Action.KICK_BROADCAST.getPermission());
	    	}
		}
	}

	@RunAsync
	public static class GKickCmd extends BATCommand {
		public GKickCmd() {
			super("gkick", "<player> [reason]", "Kick the player from the network", Action.KICK.getPermission()
					+ ".global");
		}

		@Override
		public void onCommand(final CommandSender sender, final String[] args, final boolean confirmedCmd)
				throws IllegalArgumentException {
			final String pName = args[0];
                        
            checkArgument(args.length != 1 || !BAT.getInstance().getConfiguration().isMustGiveReason(),
                    I18n.formatWithColor("noReasonInCommand"));


			final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(pName);
				checkArgument(player != null, I18n.formatWithColor("playerNotFound"));

				checkArgument(!PermissionManager.isExemptFrom(Action.KICK, pName), I18n.formatWithColor("isExempt"));

				final String returnedMsg = kick.gKick(player, sender.getName(),
					(args.length == 1) ? IModule.NO_REASON : Utils.getFinalArg(args, 1));

				BAT.broadcast(returnedMsg, Action.KICK_BROADCAST.getPermission());

		}
	}
}