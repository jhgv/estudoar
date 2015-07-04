package estudoar.cin.ufpe.br.estudoar.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import estudoar.cin.ufpe.br.estudoar.R;
import estudoar.cin.ufpe.br.estudoar.activities.DoacoesActivity;
import estudoar.cin.ufpe.br.estudoar.activities.VerDoacaoActivity;

public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    public final int MEUS_INTERESSADOS = 3;
    public final int NOTIFICAR_DOADOR = 1;
    public final int NOTIFICAR_INTERESSADO = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra("com.parse.Data");
        JSONObject json = null;
        String id_doador = "";
        String id_interessado = "";
        String name_interessado = "";
        String status = "";
        String id_doacao = "";
        int action = 0;

        try{
            json = new JSONObject(data);
            action = json.getInt("action");
            id_doador = json.getString("doador_id");
            id_interessado = json.getString("interessado_id");

            if(action == 1){
                name_interessado = json.getString("interessado_name");
            }else{
                status = json.getString("status");
                id_doacao = json.getString("doacao_id");
            }

        }catch (JSONException e){}


        //Toast.makeText(context, "O usuário : " + name_interessado + " se interessou pela sua doação!", Toast.LENGTH_SHORT).show();
        if (id_doador.equals(ParseUser.getCurrentUser().getObjectId()) && action == NOTIFICAR_DOADOR) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setVibrate(new long[] { 100, 100, 100, 100, 100 })
                            .setAutoCancel(true)
                            .setContentTitle("Novidades!")
                            .setContentText(name_interessado + " está interessado em sua doação!");

            Intent resultIntent = new Intent();
            resultIntent.setClassName("estudoar.cin.ufpe.br.estudoar", "estudoar.cin.ufpe.br.estudoar.activities.DoacoesActivity");
            resultIntent.putExtra("filter", MEUS_INTERESSADOS);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(DoacoesActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = (int) System.currentTimeMillis();
            mNotificationManager.notify(id, mBuilder.build());

        }else if(ParseUser.getCurrentUser().getObjectId().equals(id_interessado) && action == NOTIFICAR_INTERESSADO){

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_icon)
                            .setVibrate(new long[] { 100, 100, 100, 100, 100 })
                            .setAutoCancel(true)
                            .setContentTitle("Novidades!")
                            .setContentText(status);

            Intent resultIntent = new Intent();
            resultIntent.setClassName("estudoar.cin.ufpe.br.estudoar", "estudoar.cin.ufpe.br.estudoar.activities.VerDoacaoActivity");
            resultIntent.putExtra("id_doacao", id_doacao);
            resultIntent.putExtra("id_doador", id_doador);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            stackBuilder.addParentStack(VerDoacaoActivity.class);
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
