package edu.skku.cs.pp

data class Search_drink(var drinks: ArrayList<Drink>){
    data class Drink(var strDrink: String, var strTags: String, var strInstructions: String, var strDrinkThumb: String, var strCategory: String, var strAlcoholic: String, var strIngredient1: String, var strMeasure1: String, var strIngredient2: String, var strMeasure2: String, var strIngredient3: String, var strMeasure3: String, var strIngredient4: String, var strMeasure4: String, var strIngredient5: String, var strMeasure5: String, var strIngredient6: String, var strMeasure6: String, var strIngredient7: String, var strMeasure7: String, var strIngredient8: String, var strMeasure8: String, var strIngredient9: String, var strMeasure9: String)
}
data class User_token(var token: String)
data class Addlike(var token: String, var new_like: String)
data class Dellike(var token: String, var del_like: String)
data class Isuser(var ans: Boolean)
data class userinfo(var token: String, var like: ArrayList<String>)
class CoctailList(val name: String, val img_link: String)
class IngredientList(val name: String, val measure: String)