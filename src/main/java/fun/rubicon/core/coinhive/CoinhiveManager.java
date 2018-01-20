package fun.rubicon.core.coinhive;

import de.foryasee.httprequest.HttpRequest;
import de.foryasee.httprequest.RequestResponse;
import fun.rubicon.RubiconBot;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * @author Yannick Seeger / ForYaSee
 */
public class CoinhiveManager {

    private static final String BASE_URL = "https://api.coinhive.com";

    public static CoinhiveUser getCoinhiveUser(User user) {
        try {
            HttpRequest balanceRequest = prepareCoinhiveRequest("/user/balance");
            balanceRequest.addParameter("name", user.getId());
            RequestResponse balanceResponse = balanceRequest.sendGETRequest();
            JSONObject balanceObj = (JSONObject) new JSONParser().parse(balanceResponse.getResponse());
            if (balanceObj.get("success").equals("false")) {
                Logger.error((String) balanceObj.get("error"));
                return new CoinhiveUser() {
                    @Override
                    public String getName() {
                        return "Invalid user.";
                    }

                    @Override
                    public int getTotal() {
                        return 0;
                    }

                    @Override
                    public int getWithdrawn() {
                        return 0;
                    }

                    @Override
                    public long getBalance() {
                        return 0;
                    }
                };
            }
            return new CoinhiveUser() {
                @Override
                public String getName() {
                    return (String) balanceObj.get("name");
                }

                @Override
                public int getTotal() {
                    return (int) balanceObj.get("total");
                }

                @Override
                public int getWithdrawn() {
                    return (int) balanceObj.get("withdrawn");
                }

                @Override
                public long getBalance() {
                    return (long) balanceObj.get("balance");
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void withdrawUser(CoinhiveUser coinhiveUser, int amount) {
        try {
            HttpRequest balanceRequest = prepareCoinhiveRequest("/user/withdraw");
            balanceRequest.addParameter("name", coinhiveUser.getName());
            balanceRequest.addParameter("amount", amount);
            RequestResponse balanceResponse = balanceRequest.sendPOSTRequest();
            JSONObject balanceObj = (JSONObject) new JSONParser().parse(balanceResponse.getResponse());
            if (balanceObj.get("success").equals("false")) {
                Logger.error((String) balanceObj.get("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpRequest prepareCoinhiveRequest(String endpoint) {
        HttpRequest request = new HttpRequest(BASE_URL + endpoint);
        request.addParameter("secret", RubiconBot.getConfiguration().getString("coinhive_secret"));
        return request;
    }
}
