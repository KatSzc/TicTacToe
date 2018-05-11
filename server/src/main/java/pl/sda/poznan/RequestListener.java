package pl.sda.poznan;

import pl.sda.poznan.Message;

/**
 * Interfejs, ktorego zadaniem bedzie wysyalanie odpowiedniej wiadomosci na podstawie przyslanej
 * wiadomosci
 */
public interface RequestListener {

  Message onMessageReceived(Message request);
}
