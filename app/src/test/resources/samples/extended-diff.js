$(document).ready(function() {
  $('table').on('click', 'tr.function-signature .identifier', function() {
    $(this).closest('tbody').toggleClass('open');
  })
})
