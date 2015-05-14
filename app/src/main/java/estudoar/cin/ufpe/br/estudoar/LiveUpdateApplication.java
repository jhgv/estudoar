package estudoar.cin.ufpe.br.estudoar;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

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

        //Inicializa a integracao do parse com o facebook
        //FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(this);
    }
}