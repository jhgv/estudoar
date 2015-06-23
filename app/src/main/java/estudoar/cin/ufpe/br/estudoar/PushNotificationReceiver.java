package estudoar.cin.ufpe.br.estudoar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra("com.parse.Data");
        JSONObject json = null;
        String id_doador = "";
        String id_interessado = "";
        String name_interessado = "";

        try{
            json = new JSONObject(data);
            id_doador = json.getString("doador_id");
            id_interessado = json.getString("interessado_id");
            name_interessado = json.getString("interessado_name");
        }catch (JSONException e){}


        //Toast.makeText(context, "O usuário : " + name_interessado + " se interessou pela sua doação!", Toast.LENGTH_SHORT).show();
        if (id_doador.equals(ParseUser.getCurrentUser().getObjectId())) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setAutoCancel(true)
                            .setContentTitle("Alguém curtiu uma de suas doações!")
                            .setContentText("O usuário : " + name_interessado + " se interessou pela sua doação!");

            Intent resultIntent = new Intent();
            resultIntent.setClassName("estudoar.cin.ufpe.br.estudoar", "estudoar.cin.ufpe.br.estudoar.MeuPerfil");
            resultIntent.putExtra("id_usuario", id_interessado);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(MeuPerfil.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = (int) System.currentTimeMillis();
            mNotificationManager.notify(id, mBuilder.build());
        }
    }
}
