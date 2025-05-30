package me.ghosthacks96.spigot.commands;

import me.ghosthacks96.spigot.GHCore;
import me.ghosthacks96.spigot.dependant.GHCommand;
import me.ghosthacks96.spigot.utils.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

public class CoreCommand implements CommandExecutor {

    GHCore pl;
    CommandManager cmdManager;
    public CoreCommand(GHCore ghCore) {
        this.pl = ghCore;
        this.cmdManager = pl.commandManager;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String lbl,  String[] args) {

        if(commandSender.equals(pl.getServer().getConsoleSender())){
            handleConsole(args);
        }else{
            handlePlayer(commandSender,args);
        }

        return true;
    }

    private void handlePlayer(CommandSender commandSender, String[] args) {
       Player p = (Player) commandSender;
       if(args.length == 0){
           p.sendMessage(pl.prefix+"====Command Help====");
           p.sendMessage(ChatColor.AQUA+"/ghcore"+ ChatColor.GREEN+" - base command for GHCore.");
           p.sendMessage(ChatColor.GREEN+"====SubCommands====");
           for(GHCommand cmd : cmdManager.getRegisteredCommands()){
              if(p.hasPermission(cmd.getPermission())) p.sendMessage(ChatColor.GREEN+"- "+ChatColor.AQUA+cmd.getCmd()+ ChatColor.GREEN+" - "+cmd.getDesc());
           }
           p.sendMessage(pl.prefix+"=====================");
       }else{
           String cmdName = args[0];
           ArrayList<GHCommand> commands = cmdManager.getRegisteredCommands();
           for(GHCommand cmd : commands){
               if(cmd.getCmd().equalsIgnoreCase(cmdName)){
                   if(p.hasPermission(cmd.getPermission())){
                       cmd.execute(p,args);
                   }else{
                       p.sendMessage(pl.prefix+"You do not have permission to use this command!");
                   }
               }
           }
       }
    }

    private void handleConsole(String[] args) {
        if(args.length == 0){
            pl.logger.log(Level.INFO,pl.prefix,"====Command Help====");
            pl.getLogger().info(ChatColor.AQUA+"/ghcore"+ ChatColor.GREEN+" - base command for GHCore.");
            pl.getLogger().info(ChatColor.GREEN+"====SubCommands====");
            for(GHCommand cmd : cmdManager.getRegisteredCommands()){
                pl.getLogger().info(ChatColor.GREEN+"- "+ChatColor.AQUA+cmd.getCmd()+ ChatColor.GREEN+" - "+cmd.getDesc());
            }
            pl.logger.log(Level.INFO,pl.prefix,"=====================");
        }else{
            String cmdName = args[0];
            ArrayList<GHCommand> commands = cmdManager.getRegisteredCommands();
            for(GHCommand cmd : commands){
                if(cmd.getCmd().equalsIgnoreCase(cmdName)){
                    if(cmd.isPlayerOnly()){
                        pl.logger.log(Level.WARNING,pl.prefix,"This command can only be used by players!");
                    }else{
                        cmd.execute(Bukkit.getConsoleSender(),args);
                        break;
                    }
                }
            }
        }
    }
}
