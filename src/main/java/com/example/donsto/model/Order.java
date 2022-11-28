package com.example.donsto.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private
    Long id;

    @Column(name = "brand")
    private
    String brand;

    @Column(name = "model")
    private
    String model;

    @Column(name = "year_of_release")
    private
    String yearOfRelease;

    @Column(name = "description")
    private
    String description;

    @Column(name = "arrival_date")
    private
    String arrivalDate;

    @Column(name = "date")
    private
    Date date;


    public String toString() {
        return "1.Заказ_ID: " + this.getId() + "\n" +
                "2.Марка: " + this.getBrand() + "\n" +
                "3.Модель: " + this.getModel() + "\n" +
                "4.Год выпуска: " + this.getYearOfRelease() + "\n" +
                "5.Описание работ: " + this.getDescription() + "\n" +
                "6.Дата прибытия: " + this.getArrivalDate() + "\n" +
                "7.Дата записи: " + this.getDate() + "\n" + "\n";
    }
}
