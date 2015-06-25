package estudoar.cin.ufpe.br.estudoar;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class LiveUpdateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Permitir o armazenamento local das variaveis de identificacao
        Parse.enableLocalDatastore(this);

        //App e Client id
        String appId = "YUXgV0oMFzoDWkh7hgvw5T9TwRC49vvEBhZWqEQo";
        String clientId = "PCCHPtCstEbqBu3Xn5KIgNv9mEWu3OanByDsOZ01";

        //Inicializa conexao com o Parse.com
        Parse.initialize(this, appId, clientId);

        ParsePush.subscribeInBackground("broadcast", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
        PushService.setDefaultPushCallback(this, MeuPerfil.class);
        //Inicializa a integracao do parse com o facebook
        //FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
    }
}