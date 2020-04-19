// $(function(){
//     $("#loginForm").on('submit', function(e) {
//
//         e.preventDefault(); // avoid to execute the actual submit of the form.
//
//         var form = $(this);
//         var url = form.attr('action');
//
//         $.ajax({
//             type: "POST",
//             url: url,
//             data: form.serialize(), // serializes the form's elements.
//             success: function(data)
//             {
//                 $("html").html(data);
//             },
//             error: function (data) {
//                 $("#err").html("Invalid login or password!")
//             }
//         });
//
//
//     });
// });