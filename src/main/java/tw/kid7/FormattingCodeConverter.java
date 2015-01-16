package tw.kid7;

import java.io.InputStream;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class FormattingCodeConverter extends JavaPlugin implements Listener {
    //插件資訊
    protected static String pluginName, version;
    //訊息前綴
    private static String prefix = ChatColor.GOLD + "[" + ChatColor.GREEN + "FCC" + ChatColor.GOLD + "] " + ChatColor.GRAY;
    //語言
    protected static String lastLang;                //最後使用的語言
    protected static String defLang = "en_US";        //預設的語言
    public static YamlConfiguration langFile;        //語言檔
    public static YamlConfiguration defLangFile;    //預設語言檔
    //console
    ConsoleCommandSender console;

    //插件載入
    public void onEnable() {
        //插件資訊
        pluginName = getDescription().getName();
        version = getDescription().getVersion();
        //綁定事件監聽
        getServer().getPluginManager().registerEvents(this, this);
        //console
        console = getServer().getConsoleSender();
        //顯示訊息
        //getLogger().info("FormattingCodeConverter插件順利載入");
    }

    //插件卸載
    public void onDisable() {
        //顯示訊息
        //getLogger().info("FormattingCodeConverter插件順利卸載");
    }

    //指令處理
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        if (lable.equalsIgnoreCase("FormattingCodeConverter") || lable.equalsIgnoreCase("fcc")) {
            Player player = null;
            //從Console下指令
            if (!(sender instanceof Player)) {
                //sender.sendMessage(prefix + "只有遊戲中的玩家可以執行此指令");
                sender.sendMessage(prefix + "This command can only be used by player in game.");
                return true;
            } else {
                player = (Player) sender;
            }
            //玩家下指令
            if (player.hasPermission("fcc.command")) {
                if (args.length == 0) {
                    //沒有參數
                    showHelp(player);
                } else if (args.length >= 1) {
                    //有參數
                    switch (args[0]) {
                        //幫助
                        case "help":
                        case "h":
                        case "?":
                            showHelp(player);
                            break;
                        //指令方塊文字代碼符號轉換
                        case "cb":
                            //取得指向的指令方塊（忽視其他方塊）
                            //需要忽略的方塊
                            HashSet<Byte> transparent = new HashSet<Byte>();
                            transparent.add((byte) Material.AIR.getId());                //空氣
                            transparent.add((byte) Material.WALL_SIGN.getId());            //木牌（牆上）
                            transparent.add((byte) Material.SIGN.getId());                //木牌（地上）
                            transparent.add((byte) Material.STONE_BUTTON.getId());        //石頭按鈕
                            transparent.add((byte) Material.WOOD_BUTTON.getId());        //木頭按鈕
                            transparent.add((byte) Material.STONE_PLATE.getId());        //石製壓力板
                            transparent.add((byte) Material.WOOD_PLATE.getId());        //木製壓力板
                            transparent.add((byte) Material.LEVER.getId());                //拉桿
                            transparent.add((byte) Material.REDSTONE_WIRE.getId());        //紅石線
                            transparent.add((byte) Material.STATIONARY_WATER.getId());    //水
                            transparent.add((byte) Material.STATIONARY_LAVA.getId());    //岩漿
                            //距離限制
                            int maxDistance = 20;
                            //找出指向的方塊
                            //Block targetBlock = player.getTargetBlock(null, maxDistance);
                            Block targetBlock = player.getWorld().getBlockAt(player.getLocation());
                            for (Block b : player.getLineOfSight(transparent, maxDistance)) {
                                //if (!b.getType().equals(Material.AIR)) { targetBlock = b; break; }
                                //player.sendMessage(prefix + b.toString());
                                targetBlock = b;
                                if (b.getType().equals(Material.COMMAND))
                                    break;
                            }
                            //檢查是否為指令方塊
                            if (targetBlock.getType().equals(Material.COMMAND)) {
                                CommandBlock cmdBlock = (CommandBlock) targetBlock.getState();
                                //兩個參數（轉變指令方塊文字代碼格式）
                                if (args.length == 1) {
                                    //印出指令
                                    //player.sendMessage(prefix + "指令方塊：");
                                    //player.sendMessage(prefix + "Command block commands:");
                                    player.sendMessage(prefix + getLang(player, "Command-block-commands"));
                                    player.sendMessage(cmdBlock.getCommand());
                                    //後續指令
                                    //player.sendMessage(prefix + ChatColor.RED + "/fcc cb 1" + ChatColor.GRAY + ": 將" + ChatColor.GOLD + "&" + ChatColor.GRAY + "轉成" + ChatColor.GOLD + "\u00a7");
                                    //player.sendMessage(prefix + ChatColor.RED + "/fcc cb 2" + ChatColor.GRAY + ": 將" + ChatColor.GOLD + "\u00a7" + ChatColor.GRAY + "轉成" + ChatColor.GOLD + "&");
                                    //player.sendMessage(prefix + ChatColor.RED + "/fcc cb 1 " + ChatColor.GRAY + ": Convert " + ChatColor.GOLD + "& (ampersand)" + ChatColor.GRAY + " to " + ChatColor.GOLD + "\u00a7 (section symbol)");
                                    //player.sendMessage(prefix + ChatColor.RED + "/fcc cb 2 " + ChatColor.GRAY + ": Convert " + ChatColor.GOLD + "\u00a7 (section symbol)" + ChatColor.GRAY + " to " + ChatColor.GOLD + "& (ampersand)");
                                    player.sendMessage(prefix + ChatColor.RED + "/fcc cb 1 " + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-cb-1"));
                                    player.sendMessage(prefix + ChatColor.RED + "/fcc cb 2 " + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-cb-2"));
                                } else if (args.length == 2) {
                                    switch (args[1]) {
                                        case "1":
                                            //將&轉為§
                                            //cmdBlock.setCommand(cmdBlock.getCommand().replaceAll("&", "\u00a7"));
                                            cmdBlock.setCommand(ChatColor.translateAlternateColorCodes('&', cmdBlock.getCommand()));
                                            cmdBlock.update();
                                            //player.sendMessage(prefix + "轉換後的指令方塊：");
                                            //player.sendMessage(prefix + "Commands after convert:");
                                            player.sendMessage(prefix + getLang(player, "Commands-after-convert"));
                                            player.sendMessage(cmdBlock.getCommand());
                                            break;
                                        case "2":
                                            //將§轉為&
                                            cmdBlock.setCommand(cmdBlock.getCommand().replaceAll("\u00a7", "&"));
                                            cmdBlock.update();
                                            //player.sendMessage(prefix + "Commands after convert:");
                                            player.sendMessage(prefix + getLang(player, "Commands-after-convert"));
                                            player.sendMessage(cmdBlock.getCommand());
                                            break;
                                        default:
                                            wrongCommand(player);
                                    }
                                } else if (args.length > 2) {
                                    wrongCommand(player);
                                }
                            } else {
                                if (!targetBlock.getType().equals(Material.AIR)) {
                                    //player.sendMessage(prefix + "目標方塊：" + targetBlock.getType());
                                    //player.sendMessage(prefix + "Target block: " + targetBlock.getType());
                                    player.sendMessage(prefix + getLang(player, "Target-block") + targetBlock.getType());
                                }
                                //player.sendMessage(prefix + "必須指向指令方塊");
                                //player.sendMessage(prefix + "You must target (look at) a command block.");
                                player.sendMessage(prefix + getLang(player, "You-must-target-a-command-block"));
                            }
                            break;
                        //關於
                        case "about":
                            showAbout(player);
                            break;
                        default:
                            wrongCommand(player);
                    }
                }
                return true;
            } else {
                //player.sendMessage(prefix + "權限不足");
                //player.sendMessage(prefix + "You don't have permission.");
                player.sendMessage(prefix + getLang(player, "No-permission"));
                return true;
            }
        }
        return false;
    }

    //顯示說明
    private void showHelp(Player player) {
        /*player.sendMessage(prefix + "FormattingCodeConverter說明");
        player.sendMessage(ChatColor.RED + "/fcc " + ChatColor.GRAY + ": 指令清單");
        player.sendMessage(ChatColor.RED + "/fcc help " + ChatColor.GRAY + ": 指令清單");
        player.sendMessage(ChatColor.RED + "/fcc cb " + ChatColor.GRAY + ": 查詢指令方塊的指令");
        player.sendMessage(ChatColor.RED + "/fcc cb 1" + ChatColor.GRAY + ": 將" + ChatColor.GOLD + "&" + ChatColor.GRAY + "轉成" + ChatColor.GOLD + "§");
        player.sendMessage(ChatColor.RED + "/fcc cb 2" + ChatColor.GRAY + ": 將" + ChatColor.GOLD + "§" + ChatColor.GRAY + "轉成" + ChatColor.GOLD + "&");
        player.sendMessage(ChatColor.RED + "/fcc about " + ChatColor.GRAY + ": 關於此插件");*/
        /*player.sendMessage(prefix + ChatColor.WHITE + pluginName + " - Help");
        player.sendMessage(ChatColor.RED + "/fcc " + ChatColor.GRAY + ": Show list of commands");
        player.sendMessage(ChatColor.RED + "/fcc help " + ChatColor.GRAY + ": Show list of commands");
        player.sendMessage(ChatColor.RED + "/fcc cb " + ChatColor.GRAY + ": Show command block commands");
        player.sendMessage(ChatColor.RED + "/fcc cb 1 " + ChatColor.GRAY + ": Convert " + ChatColor.GOLD + "& (ampersand)" + ChatColor.GRAY + " to " + ChatColor.GOLD + "\u00a7 (section symbol)");
        player.sendMessage(ChatColor.RED + "/fcc cb 2 " + ChatColor.GRAY + ": Convert " + ChatColor.GOLD + "\u00a7 (section symbol)" + ChatColor.GRAY + " to " + ChatColor.GOLD + "& (ampersand)");
        player.sendMessage(ChatColor.RED + "/fcc about " + ChatColor.GRAY + ": About " + pluginName);*/

        player.sendMessage(prefix + ChatColor.WHITE + pluginName + " - " + getLang(player, "Help.title"));
        player.sendMessage(ChatColor.RED + "/fcc" + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-help"));
        player.sendMessage(ChatColor.RED + "/fcc help" + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-help"));
        player.sendMessage(ChatColor.RED + "/fcc cb " + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-cb"));
        player.sendMessage(ChatColor.RED + "/fcc cb 1 " + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-cb-1"));
        player.sendMessage(ChatColor.RED + "/fcc cb 2 " + ChatColor.GRAY + ": " + getLang(player, "Help.fcc-cb-2"));
        player.sendMessage(ChatColor.RED + "/fcc about " + ChatColor.GRAY + ": " + getLang(player, "Help.about") + " " + pluginName);
        //showLanguage(player);
    }

    //顯示關於
    private void showAbout(Player player) {
        //player.sendMessage(prefix + "關於FormattingCodeConverter");
        /*player.sendMessage(prefix + ChatColor.WHITE + pluginName + " - About");
        player.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.GRAY + version);
        player.sendMessage(ChatColor.YELLOW + "Developer: " + ChatColor.GRAY + "KID(jyhsu)");*/
        player.sendMessage(prefix + ChatColor.WHITE + pluginName + " - " + getLang(player, "About.title"));
        player.sendMessage(ChatColor.YELLOW + getLang(player, "About.Version") + ": " + ChatColor.GRAY + version);
        player.sendMessage(ChatColor.YELLOW + getLang(player, "About.Developer") + ": " + ChatColor.GRAY + "KID(jyhsu)");
        player.sendMessage(ChatColor.GRAY + "http://fb.me/IMCEIMCE");
        player.sendMessage(ChatColor.GRAY + "http://dev.bukkit.org/profiles/jyhsu");
    }

    //指令錯誤
    private void wrongCommand(Player player) {
        //player.sendMessage(prefix + "指令錯誤");
        //player.sendMessage(prefix + "輸入" + ChatColor.RED + "/" + lable + " help" + ChatColor.GRAY + "取得更多協助");
        /*player.sendMessage(prefix + "Wrong command!");
        player.sendMessage(prefix + "Type " + ChatColor.RED + "/fcc" + ChatColor.GRAY + " for help.");*/
        player.sendMessage(prefix + getLang(player, "Wrong-command"));
        player.sendMessage(prefix + getLang(player, "More-help"));
    }

    //取得語言包
    private String getLang(Player player, String path) {
        String result;
        //載入預設語言
        if (defLangFile == null) {
            try {
                InputStream defConfigStream = this.getResource("lang_" + defLang + ".yml");
                if (defConfigStream != null) {
                    defLangFile = YamlConfiguration.loadConfiguration(defConfigStream);
                }
                //console.sendMessage(prefix + "Loaded default language file.");
            } catch (Exception e) {
                console.sendMessage(prefix + ChatColor.RED + "Loaded default language file Failed.");
            }
        }
        //觸發指令的玩家的語言，是否與最後一次觸發的語言相同
        if (langFile == null || !lastLang.equals(Language.getLanguage(player).getCode())) {
            //更新語言
            lastLang = Language.getLanguage(player).getCode();
            //重新載入語言檔
            try {
                InputStream defConfigStream = this.getResource("lang_" + Language.getLanguage(player).getCode() + ".yml");
                if (defConfigStream != null) {
                    langFile = YamlConfiguration.loadConfiguration(defConfigStream);
                    //console.sendMessage(prefix + "Loaded " + lastLang + " language file.");
                } else {
                    langFile = defLangFile;
                    //console.sendMessage(prefix + lastLang + " language file does not exist. Loaded default language file.");
                }
            } catch (Exception e) {
                console.sendMessage(prefix + ChatColor.RED + "Loaded " + lastLang + " language file Failed.");
            }
        }
        //回傳字串
        if (langFile != null && langFile.contains(path)) {
            result = langFile.getString(path);
        } else if (defLangFile != null && defLangFile.contains(path)) {
            result = defLangFile.getString(path);
        } else {
            result = "LANG." + path;
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    //顯示語言
    private void showLanguage(Player player) {
        player.sendMessage(ChatColor.RED + "Language: " + Language.getLanguage(player));
        player.sendMessage(ChatColor.RED + "Language Code: " + Language.getLanguage(player).getCode());
        player.sendMessage(ChatColor.RED + "Language Name: " + Language.getLanguage(player).getName());
    }
}
